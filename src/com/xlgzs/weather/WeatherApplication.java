package com.xlgzs.weather;

import com.thinkland.sdk.android.JuheSDKInitializer;

import android.app.Application;

public class WeatherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JuheSDKInitializer.initialize(getApplicationContext());
    }

}
