package com.simple.base.application;


import android.app.Application;
import android.content.Context;
import android.support.annotation.CallSuper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAbstractApplication extends Application {
    private List<Class<? extends ApplicationLogic>> logicClasses;
    private List<ApplicationLogic> logics;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        logicClasses = new ArrayList<>();
        logics = new ArrayList<>();
        initLogic();
        logicAttach(base);
    }

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();
        logicCreate();
    }

    /**
     * 在主Module的application中调用,以注册各个Module中的Application到logicClasses中
     */
    protected abstract void initLogic();

    public void registerBaseApplicationLogic(Class<? extends ApplicationLogic>... logicClass) {
        logicClasses.addAll(Arrays.asList(logicClass));
    }


    private void logicCreate() {
        for (ApplicationLogic aClass : logics) {
            aClass.onCreate();
        }
    }


    private void logicAttach(Context base) {
        for (Class<? extends ApplicationLogic> aClass : logicClasses) {
            try {
                ApplicationLogic baseApplicationLogic = aClass.newInstance();
                baseApplicationLogic.attachBaseContext(base);
                logics.add(baseApplicationLogic);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }


}
