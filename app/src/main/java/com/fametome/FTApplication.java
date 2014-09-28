package com.fametome;

import android.app.Application;

import com.fametome.activity.member.MainActivity;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class FTApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(getApplicationContext(), "onturXZAM4c3z4z7xXRJA93OtGBALa6Olen3n1xb", "F5U4m1BebeZA1D17naEf7iQR9VKcamaQrwkGos1m");
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }

}
