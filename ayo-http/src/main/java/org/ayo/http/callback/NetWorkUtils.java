package org.ayo.http.callback;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtils {
	
	public static boolean isConnected(Context context) {
		boolean isWIFI = isWIFI(context);
		boolean isMobile = isMobile(context);

		if (isWIFI == false && isMobile == false) {
			return false;
		}
		
		return true;
	}
	

	public static boolean isWIFI(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	public static boolean isMobile(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

}
