package com.marverenic.music;

import android.app.Application;

import com.squareup.picasso.Picasso;


public class JockeyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Picasso.setSingletonInstance(new Picasso.Builder(this).indicatorsEnabled(true).build());
        }

        PlayerController.startService(getApplicationContext());
    }
}
