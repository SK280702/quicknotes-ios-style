package com.quicknotes.app;
import android.app.*;import android.content.*;import android.os.*;import android.webkit.*;import org.json.*;import java.util.concurrent.*;import java.util.concurrent.atomic.*;
/** Runs only in a separately installed test APK, never in the user release. */
public class NativeTests extends Instrumentation {
 Context context;MainActivity activity;
 void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
 @Override public void onCreate(Bundle args){super.onCreate(args);start();}
 JSONObject data(long due)throws Exception{return new JSONObject("{\"version\":1,\"lists\":[{\"id\":\"default\",\"title\":\"Test\",\"updated\":1}],\"items\":[]}").put("items",new JSONArray().put(new JSONObject().put("id","native-alarm-test").put("title","Native alarm verification").put("list","default").put("updated",System.currentTimeMillis()).put("due",due).put("timeEnabled",true).put("urgent",true)));}
 void store(JSONObject d)throws Exception{synchronized(MainActivity.STORE_LOCK){Reminders.write(context,d);Reminders.schedule(context,d);}}
 boolean ringing(){return AlarmService.current!=null&&AlarmService.current.player!=null&&AlarmService.current.player.isPlaying();}
 void awaitRing(boolean expected,long wait)throws Exception{long end=System.currentTimeMillis()+wait;while(System.currentTimeMillis()<end){if(ringing()==expected)return;Thread.sleep(100);}check(ringing()==expected,"Unexpected alarm audio state, expected "+expected);}
 void confirmDeletion()throws Exception{
  CountDownLatch complete=new CountDownLatch(1);AtomicReference<String> result=new AtomicReference<>();AtomicReference<AlertDialog> dialog=new AtomicReference<>();
  runOnMainSync(()->activity.web.evaluateJavascript("confirm('Delete native test reminder?')",s->{result.set(s);complete.countDown();}));
  long end=System.currentTimeMillis()+5000;while(dialog.get()==null&&System.currentTimeMillis()<end){runOnMainSync(()->dialog.set(activity.lastConfirm));Thread.sleep(100);}
  check(dialog.get()!=null&&dialog.get().isShowing(),"WebView deletion confirmation did not appear");runOnMainSync(()->dialog.get().getButton(AlertDialog.BUTTON_POSITIVE).performClick());check(complete.await(5,TimeUnit.SECONDS),"Confirmation callback did not finish");check("true".equals(result.get()),"Confirmation did not return true");
 }
 @Override public void onStart(){Bundle result=new Bundle();try{context=getTargetContext();activity=(MainActivity)startActivitySync(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));waitForIdleSync();Thread.sleep(1500);confirmDeletion();
  check(Reminders.exactAllowed(context),"Exact alarm permission is not granted");
  // Exact alarm must fire while the activity is backgrounded and play actual alarm audio.
  JSONObject d=data(System.currentTimeMillis()+4000);store(d);runOnMainSync(()->activity.moveTaskToBack(true));awaitRing(true,15000);
  check(AlarmService.current.player.isLooping(),"Urgent alarm audio is not looping");check(AlarmService.current.wake!=null&&AlarmService.current.wake.isHeld(),"Alarm wakelock is absent");
  // Dismiss stops ringing without completing or cloning the reminder.
  context.startService(AlarmService.command(context,"STOP","native-alarm-test"));awaitRing(false,5000);JSONObject saved=Reminders.read(context);check(saved.getJSONArray("items").length()==1,"Dismiss duplicated reminder");check(!saved.getJSONArray("items").getJSONObject(0).optBoolean("done"),"Dismiss completed reminder unexpectedly");
  // A second firing can be snoozed without changing the reminder ID or source due date.
  d=data(System.currentTimeMillis()+3000);store(d);awaitRing(true,15000);context.startService(AlarmService.command(context,"SNOOZE","native-alarm-test"));awaitRing(false,5000);check(context.getSharedPreferences(Reminders.PREFS,0).getLong("snooze:native-alarm-test",0)>System.currentTimeMillis()+250000,"Snooze did not schedule five minutes");
  // Completing cancels snooze and any future urgent alarm; subsequent deletion cancels it too.
  d=data(System.currentTimeMillis()+3000);d.getJSONArray("items").getJSONObject(0).put("done",true);store(d);Thread.sleep(5000);check(!ringing(),"Completed reminder alarm rang");check(context.getSharedPreferences(Reminders.PREFS,0).getLong("snooze:native-alarm-test",0)==0,"Completion retained snooze");
  d=data(System.currentTimeMillis()+3000);store(d);d.getJSONArray("items").getJSONObject(0).put("deleted",true);store(d);Thread.sleep(5000);check(!ringing(),"Deleted reminder alarm rang");
  // Recurrence reactivates the existing record, preserving ID and row count.
  d=data(System.currentTimeMillis()-10000);JSONObject item=d.getJSONArray("items").getJSONObject(0);item.put("done",true).put("repeat","daily").put("nextDue",System.currentTimeMillis()+3000);store(d);awaitRing(true,15000);saved=Reminders.read(context);check(saved.getJSONArray("items").length()==1,"Native recurrence duplicated reminder");check(!saved.getJSONArray("items").getJSONObject(0).getBoolean("done"),"Native recurrence did not reactivate");context.startService(AlarmService.command(context,"STOP","native-alarm-test"));awaitRing(false,5000);
  result.putString("stream","Native alarm and confirmation tests passed\n");finish(Activity.RESULT_OK,result);
 }catch(Throwable error){result.putString("stream","FAILED: "+android.util.Log.getStackTraceString(error));finish(Activity.RESULT_CANCELED,result);}finally{if(activity!=null)runOnMainSync(()->activity.finish());}}
}
