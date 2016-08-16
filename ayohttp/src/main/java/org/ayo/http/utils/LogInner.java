package org.ayo.http.utils;

import android.util.Log;

class LogInner {
	
	
	public static void print(String s){
		System.out.println("Genius: " + s);
	}
	
	public static void debug(String msg){
		if(msg == null) msg = "null";
		Log.i("ayo", msg);
	}
}
