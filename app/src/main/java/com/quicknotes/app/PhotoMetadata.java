package com.quicknotes.app;
import android.content.Context;import android.os.Bundle;import android.graphics.*;import org.json.*;import java.io.File;
/** Hidden extras for notification-listener apps. No large icon or visible photo style. */
final class PhotoMetadata {
 static Bundle extras(Context c,JSONObject item){Bundle b=new Bundle();try{JSONArray a=item.optJSONArray("attachments");if(a==null)return b;for(int n=0;n<a.length();n++){JSONObject f=a.getJSONObject(n);if(!f.optString("mime").startsWith("image/")||!MainActivity.safeId(f.optString("id")))continue;String id=f.getString("id"),path=new File(c.getFilesDir(),"attachments/"+id).getPath();BitmapFactory.Options o=new BitmapFactory.Options();o.inJustDecodeBounds=true;BitmapFactory.decodeFile(path,o);if(o.outWidth<1)return b;o.inSampleSize=1;while(o.outWidth/o.inSampleSize>128||o.outHeight/o.inSampleSize>128)o.inSampleSize*=2;o.inJustDecodeBounds=false;Bitmap image=BitmapFactory.decodeFile(path,o);b.putString("quicknotes.photoId",id);b.putString("quicknotes.photoMime",f.optString("mime"));if(image!=null)b.putParcelable("quicknotes.contactPhoto",image);break;}}catch(Exception ignored){}return b;}
}
