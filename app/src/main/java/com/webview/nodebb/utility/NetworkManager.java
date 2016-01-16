package com.webview.nodebb.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


// android.permission.ACCESS_NETWORK_STATE
public class NetworkManager {
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
    }


    public static int getType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            // Returns ConnectivityManager.TYPE_WIFI or ConnectivityManager.TYPE_MOBILE
            return networkInfo.getType();
        } else {
            return -1;
        }
    }


    public static String getTypeName(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getTypeName();
        } else {
            return null;
        }
    }
}
