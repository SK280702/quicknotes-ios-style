package com.quicknotes.app;
import org.json.*;import java.util.*;
/** Content expires; minimal tombstones remain to keep old devices from resurrecting it. */
final class Retention {
 static final long DAYS=30L*86400000;
 static void purge(JSONObject d,long now)throws Exception{JSONArray a=d.getJSONArray("items");for(int n=0;n<a.length();n++){JSONObject i=a.getJSONObject(n);if(!i.optBoolean("deleted")||i.optBoolean("purged")||i.optLong("deletedAt")>now-DAYS)continue;String id=i.getString("id");long updated=i.getLong("updated"),at=i.optLong("deletedAt");List<String> keys=new ArrayList<>();Iterator<String> it=i.keys();while(it.hasNext())keys.add(it.next());for(String key:keys)i.remove(key);i.put("id",id).put("updated",Math.max(now,updated+1)).put("deleted",true).put("purged",true).put("deletedAt",at);}}
}
