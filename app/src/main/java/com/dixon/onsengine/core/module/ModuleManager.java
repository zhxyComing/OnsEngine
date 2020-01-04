package com.dixon.onsengine.core.module;

import android.content.res.Configuration;

import com.dixon.onsengine.core.monitor.AppStateTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * module分发基类
 */
public class ModuleManager implements AppStateTracker.AppStateChangeListener {

    private List<String> modules = new ArrayList<>(); //模块类全限定名
    protected HashMap<String, AbsModule> allModules = new HashMap<>(); //模块实体

    public List<String> getModuleNames() {
        return modules;
    }

    public void moduleConfig(List<String> modules) {
        this.modules = modules;
    }

    public AbsModule getModuleByName(String name) {
        if (!allModules.isEmpty()) {
            return allModules.get(name);
        }
        return null;
    }

    public void onResume() {
        for (AbsModule module : allModules.values()) {
            if (module != null) {
                module.onResume();
            }
        }
    }

    public void onPause() {
        for (AbsModule module : allModules.values()) {
            if (module != null) {
                module.onPause();
            }
        }
    }

    public void onStop() {
        for (AbsModule module : allModules.values()) {
            if (module != null) {
                module.onStop();
            }
        }
    }

    public void onDestroy() {
        for (AbsModule module : allModules.values()) {
            if (module != null) {
                module.onDestroy();
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        for (AbsModule module : allModules.values()) {
            if (module != null) {
                module.onOrientationChanges(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
            }
        }
    }

    @Override
    public void appTurnIntoForeground() {
        for (AbsModule module : allModules.values()) {
            if (module != null) {
                module.appTurnIntoForeground();
            }
        }
    }

    @Override
    public void appTurnIntoBackGround() {
        for (AbsModule module : allModules.values()) {
            if (module != null) {
                module.appTurnIntoBackGround();
            }
        }
    }
}