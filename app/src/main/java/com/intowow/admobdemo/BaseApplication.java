package com.intowow.admobdemo;

import android.app.Application;

import com.intowow.sdk.I2WAPI;

/**
 * if your project has already used another Application class,
 * then you can also copy these code to your Application class
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        //	[Notice]
        //	init SDK
        //
        I2WAPI.init(this);
    }
}
