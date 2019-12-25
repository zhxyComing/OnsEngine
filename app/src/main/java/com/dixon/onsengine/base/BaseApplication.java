package com.dixon.onsengine.base;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;


import com.dixon.onsengine.core.monitor.AppStateRegister;
import com.dixon.onsengine.core.monitor.AppStateTracker;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class BaseApplication extends Application implements AppStateTracker.AppStateChangeListener, IGetActivity {

    private static BaseApplication sApplication;
    private static Activity sTopActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        // 子类初始化
        onInitSpeed();

        AppStateTracker.track(this, this);
        registerTopActivityMonitor();

        firstInAppLogic();
        // 子类初始化
        onInitLow();
    }

    private void firstInAppLogic() {
        SharedPreferences shared = getSharedPreferences("is_first_in", MODE_PRIVATE);
        boolean isFirstIn = shared.getBoolean("first_in", true);
        SharedPreferences.Editor editor = shared.edit();
        if (isFirstIn) {
            //第一次进入跳转
            editor.putBoolean("first_in", false);
            editor.apply();
            onFirstInApp();
        }
    }

    protected abstract void onInitSpeed();

    protected abstract void onInitLow();

    protected abstract void onFirstInApp();

    public static BaseApplication getApplication() {
        return sApplication;
    }

    @Override
    public void appTurnIntoForeground() {
        List<WeakReference<AppStateTracker.AppStateChangeListener>> list = AppStateRegister.getRegisterTrackers();
        for (WeakReference<AppStateTracker.AppStateChangeListener> reference : list) {
            AppStateTracker.AppStateChangeListener listener = reference.get();
            if (listener != null) {
                listener.appTurnIntoForeground();
            }
        }
    }

    @Override
    public void appTurnIntoBackGround() {
        List<WeakReference<AppStateTracker.AppStateChangeListener>> list = AppStateRegister.getRegisterTrackers();
        for (WeakReference<AppStateTracker.AppStateChangeListener> reference : list) {
            AppStateTracker.AppStateChangeListener listener = reference.get();
            if (listener != null) {
                listener.appTurnIntoBackGround();
            }
        }
    }

    @Override
    public Activity getTopActivity() {
        return sTopActivity;
    }

    private void registerTopActivityMonitor() {

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                sTopActivity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                //根据生命周期 A.resume > B.destroy 所以不会导致内存泄漏
                sTopActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
