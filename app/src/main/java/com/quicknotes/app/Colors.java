package com.quicknotes.app;
import android.graphics.Color;
final class Colors {
 static double linear(int c){double n=c/255.0;return n<=.04045?n/12.92:Math.pow((n+.055)/1.055,2.4);}
 static int text(int background){double l=.2126*linear(Color.red(background))+.7152*linear(Color.green(background))+.0722*linear(Color.blue(background));return l>.179?Color.BLACK:Color.WHITE;}
 static int parse(String value,int fallback){try{return value!=null&&value.matches("#[0-9a-fA-F]{6}")?Color.parseColor(value):fallback;}catch(Exception e){return fallback;}}
}
