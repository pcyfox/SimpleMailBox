package com.simple.base.application;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;

public class ApplicationLogic {
    public ApplicationLogic(){}
    protected BaseAbstractApplication application;

    public BaseAbstractApplication getApplication() {
        return application;
    }

    public void setApplication(BaseAbstractApplication application) {
        this.application = application;
    }
    protected void attachBaseContext(Context base) {

    }

    public void onCreate() {
    }


    public void onTerminate() {
    }


    public void onConfigurationChanged(Configuration newConfig) {
    }


    public void onLowMemory() {

    }


    public void onTrimMemory(int level) {
    }


    public void registerComponentCallbacks(ComponentCallbacks callback) {

    }


    public void unregisterComponentCallbacks(ComponentCallbacks callback) {

    }


    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {

    }


    public void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {

    }


    public void registerOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {

    }


    public void unregisterOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {

    }
}
