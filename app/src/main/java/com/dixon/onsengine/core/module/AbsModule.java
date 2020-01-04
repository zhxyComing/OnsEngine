package com.dixon.onsengine.core.module;

import android.os.Bundle;

import com.dixon.onsengine.core.monitor.AppStateTracker;

/**
 * 抽象 module
 */
public abstract class AbsModule implements AppStateTracker.AppStateChangeListener {

    //初始化将AbsModule的参数传入
    public abstract void init(ModuleContext moduleContext);

    public abstract void onSaveInstanceState(Bundle outState);

    public abstract void onResume();

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onOrientationChanges(boolean isLandscape);

    public abstract void onDestroy();
}