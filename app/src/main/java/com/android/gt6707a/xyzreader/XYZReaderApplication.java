package com.android.gt6707a.xyzreader;

import android.app.Application;

import timber.log.Timber;

public class XYZReaderApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
