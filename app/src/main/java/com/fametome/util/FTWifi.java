package com.fametome.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class FTWifi {

    public static boolean isNetworkAvailable(Context context) {

        if(context == null){
            return true;
        }

        ConnectivityManager connectivityManager  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
        if (info != null){
            for (int i = 0; i < info.length; i++){
                if (info[i].getState() == NetworkInfo.State.CONNECTED){
                    Log.v("Wifi", "Wifi is available");
                    return true;
                }
            }
        }

        Log.v("FTWifi", "Wifi is not available");

        return false;
    }

}
