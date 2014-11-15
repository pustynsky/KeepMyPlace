package com.delphis.keepmyplace.utilites;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class UtilitesConnection {

	private static String TAG="Connection";
	
	private static boolean isNetworkAvailable(Context ctx) {
	    ConnectivityManager connectivityManager 
	         = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	public static boolean hasInternetAccess(Context context) {
	    if (isNetworkAvailable(context)) {
	        try {
	            HttpURLConnection urlc = (HttpURLConnection) 
	                (new URL("http://clients3.google.com/generate_204")
	                .openConnection());
	            urlc.setRequestProperty("User-Agent", "Android");
	            urlc.setRequestProperty("Connection", "close");
	            urlc.setConnectTimeout(1500); 
	            urlc.connect();
	            return (urlc.getResponseCode() == 204 &&
	                        urlc.getContentLength() == 0);
	        } catch (IOException e) {
	            Log.e(TAG, "Error checking internet connection", e);
	        }
	    } else {
	        Log.d(TAG, "No network available!");
	    }
	    return false;
	}
}
