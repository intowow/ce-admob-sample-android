package com.intowow.admobdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.intowow.admobdemo.common.Config;
import com.intowow.sdk.I2WAPI;

/**
 * if your project has already used another Application class,
 * then you can also copy these code to your Application class
 */
public class BaseApplication extends Application {

    private static final boolean IS_SUPPORT_LIFECYCLE_CALLBACKS =
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH);

    private static BaseApplication mInstance = null;

    @Override
    public void onCreate() {

        super.onCreate();
        mInstance = this;

        //	register every activity's life-cycle here,
        //
        if (IS_SUPPORT_LIFECYCLE_CALLBACKS) {
            mInstance.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

                @Override
                public void onActivityCreated(Activity activity,
                                              Bundle savedInstanceState) {
                    //	[Notice]
                    //	init SDK
                    //
                    //	production mode
                    //
                    //I2WAPI.init(activity);

                    //	Test Mode
                    //
                    //I2WAPI.init(activity, true);

                    //	or open verbose log with test mode
                    //
                    I2WAPI.init(activity, Config.DEFAULT_TEST_MODE, Config.DEFAULT_VERBOSE_LOG);
                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                    //	[Notice]
                    //	init SDK
                    //
                    I2WAPI.onActivityResume(activity);

                }

                @Override
                public void onActivityPaused(Activity activity) {

                    //	[Notice]
                    //	init SDK
                    //
                    I2WAPI.onActivityPause(activity);
                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity,
                                                        Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                }
            });
        }

    }
}
