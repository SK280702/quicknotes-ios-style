package com.quicknotes.app;
import com.google.mlkit.nl.entityextraction.*;
import org.json.*;
import java.util.*;
/** ML Kit runs locally after its language model has downloaded. Never submits partial speech. */
final class CaptureParser {
 private static final Map<MainActivity,EntityExtractor> clients=new WeakHashMap<>();
 static void parse(MainActivity a,String text){
  if(text==null||text.trim().isEmpty()||text.length()>4000){a.js("onCaptureError","Describe a reminder using 1–4,000 characters.");return;}
  EntityExtractor client=clients.get(a);
  if(client==null){client=EntityExtraction.getClient(new EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH).build());clients.put(a,client);}
  final EntityExtractor extractor=client;
  a.js("onCaptureStatus","Preparing local language model…");
  extractor.downloadModelIfNeeded().addOnSuccessListener(v->{
   if(a.destroyed)return;
   EntityExtractionParams params=new EntityExtractionParams.Builder(text).setReferenceTime(System.currentTimeMillis()).setReferenceTimeZone(TimeZone.getDefault()).build();
   extractor.annotate(params).addOnSuccessListener(annotations->{
    try{JSONArray entities=new JSONArray();
     for(EntityAnnotation annotation:annotations)for(Entity e:annotation.getEntities())if(e.getType()==Entity.TYPE_DATE_TIME){DateTimeEntity date=e.asDateTimeEntity();entities.put(new JSONObject().put("start",annotation.getStart()).put("end",annotation.getEnd()).put("timestamp",date.getTimestampMillis()).put("granularity",date.getDateTimeGranularity()));}
     a.js("onCaptureParsed",new JSONObject().put("text",text).put("entities",entities));
    }catch(Exception e){a.js("onCaptureError","Could not read the extracted date. Please try again.");}
   }).addOnFailureListener(e->a.js("onCaptureError","Local parsing failed. You can retry or create a reminder manually."));
  }).addOnFailureListener(e->a.js("onCaptureError","The local language model needs a one-time download. Connect to the internet and retry, or create a reminder manually."));
 }
 static void close(MainActivity a){EntityExtractor client=clients.remove(a);if(client!=null)client.close();}
}
