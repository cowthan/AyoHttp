package org.ayo.http.utils;


import java.io.File;
import java.util.Map;


public class HttpHelper {
	
	
	public static final String HTTP_ERROR_INTO_PREFIX = "---http-fail---:";
	
	public static boolean isHttpCodeOK(int code){
		return code >= 200 && code < 300;
	}
	
	public static String makeURL(String p_url, Map<String, String> params) {
		StringBuilder url = new StringBuilder(p_url);
		if(url.indexOf("?")<0)
			url.append('?');

		for(String name : params.keySet()){
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
		}

		return url.toString().replace("?&", "?");
	}
	
    public static void printMap(String suffix, final Map<String, String> map){
    	try {

			if(map == null) return;
			for(String key: map.keySet()){
				String value = map.get(key) + "";
				LogInner.debug(suffix + key + "==>" + value);
			}

		} catch (Exception ignore) {
		}
    	
    }

    public static String getMediaType(File f){
		return "application/json; charset=utf-8";
	}

}
