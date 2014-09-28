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

        Log.v("Wifi", "start");

        ConnectivityManager connectivityManager  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Log.v("Wifi", "afterManager");

        NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
        if (info != null){
            for (int i = 0; i < info.length; i++){
                if (info[i].getState() == NetworkInfo.State.CONNECTED){
                    Log.v("Wifi", "Wifi is available");
                    return true;
                }
            }
        }

        Log.v("Wifi", "Wifi is not available");

        return false;
    }

}
