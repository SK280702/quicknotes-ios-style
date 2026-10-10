package com.quicknotes.app;
import android.content.*;import android.database.*;import android.net.Uri;import android.os.ParcelFileDescriptor;import android.provider.OpenableColumns;import org.json.*;import java.io.*;
public class AttachmentProvider extends ContentProvider {
 public boolean onCreate(){return true;}
 File file(Uri u)throws FileNotFoundException {String id=u.getLastPathSegment();if(!MainActivity.safeId(id))throw new FileNotFoundException();return new File(getContext().getFilesDir(),"attachments/"+id);}
 JSONObject meta(Uri u){try{byte[] b;try(InputStream in=new FileInputStream(new File(getContext().getFilesDir(),"data.json"))){b=MainActivity.bytes(in,MainActivity.MAX_BUNDLE);}JSONArray items=new JSONObject(new String(b,"UTF-8")).getJSONArray("items");for(int n=0;n<items.length();n++){JSONArray a=items.getJSONObject(n).optJSONArray("attachments");if(a!=null)for(int k=0;k<a.length();k++)if(u.getLastPathSegment().equals(a.getJSONObject(k).optString("id")))return a.getJSONObject(k);}}catch(Exception e){}return new JSONObject();}
 public String getType(Uri u){return meta(u).optString("mime","application/octet-stream");}
 public Cursor query(Uri u,String[] p,String s,String[] a,String o){try{File f=file(u);JSONObject m=meta(u);String[] cols=p==null?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:p;MatrixCursor c=new MatrixCursor(cols);Object[] row=new Object[cols.length];for(int n=0;n<cols.length;n++)row[n]=cols[n].equals(OpenableColumns.DISPLAY_NAME)?m.optString("name","Attachment"):cols[n].equals(OpenableColumns.SIZE)?f.length():null;c.addRow(row);return c;}catch(Exception e){return null;}}
 public ParcelFileDescriptor openFile(Uri u,String mode)throws FileNotFoundException {if(!mode.equals("r"))throw new FileNotFoundException("Read only");return ParcelFileDescriptor.open(file(u),ParcelFileDescriptor.MODE_READ_ONLY);}
 public Uri insert(Uri u,ContentValues v){throw new UnsupportedOperationException();}public int update(Uri u,ContentValues v,String s,String[] a){throw new UnsupportedOperationException();}public int delete(Uri u,String s,String[] a){throw new UnsupportedOperationException();}
}
