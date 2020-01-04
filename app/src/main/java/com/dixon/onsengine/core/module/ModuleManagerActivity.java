package com.dixon.onsengine.core.module;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import com.dixon.onsengine.base.BaseActivity;
import com.dixon.onsengine.core.monitor.AppStateTracker;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ModuleManagerActivity extends BaseActivity implements AppStateTracker.AppStateChangeListener {

    private ActivityModuleManager moduleManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //布局 onLayout 时初始化
        ViewTreeObserver viewTreeObserver = getWindow().getDecorView().getRootView().getViewTreeObserver();
        viewTreeObserver.addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
            @Override
            public void onWindowAttached() {
                if (moduleManager == null) {
                    initModuleManager(savedInstanceState);
                }
            }

            @Override
            public void onWindowDetached() {

            }
        });
    }

    private void initModuleManager(Bundle saveInstance) {
        moduleManager = new ActivityModuleManager();
        moduleManager.initModules(saveInstance, this, moduleConfig());
    }

    public abstract HashMap<String, ArrayList<Integer>> moduleConfig();

    @Override
    protected void onResume() {
        super.onResume();
        if (moduleManager != null) {
            moduleManager.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (moduleManager != null) {
            moduleManager.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (moduleManager != null) {
            moduleManager.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (moduleManager != null) {
            moduleManager.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void appTurnIntoForeground() {
        if (moduleManager != null) {
            moduleManager.appTurnIntoForeground();
        }
    }

    @Override
    public void appTurnIntoBackGround() {
        if (moduleManager != null) {
            moduleManager.appTurnIntoBackGround();
        }
    }
}