package com.outliers.android.flingme.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.outliers.android.flingme.constants.AppConstants;

/**
 * Created by outliersasu on 11/22/17.
 */

public class FlingMeApplication extends Application {

    public SharedPreferences appPref;

    @Override
    public void onCreate(){
        super.onCreate();

        appPref = getSharedPreferences(AppConstants.APP_PREF_NAME,MODE_PRIVATE);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("Uncaught",Log.getStackTraceString(e));
            }
        });
    }
}
