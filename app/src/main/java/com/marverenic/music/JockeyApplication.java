package com.marverenic.music;

import android.app.Application;

import com.bumptech.glide.Glide;

public class JockeyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PlayerController.startService(getApplicationContext());
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.with(this).onTrimMemory(level);
    }
}
