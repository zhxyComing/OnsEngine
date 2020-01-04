package com.dixon.onsengine.core.module;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.collection.SparseArrayCompat;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity Module 分发管理者
 */
public class ActivityModuleManager extends ModuleManager {

    public void initModules(Bundle saveInstance, Activity activity, HashMap<String, ArrayList<Integer>> modules) {
        if (activity == null || modules == null) {
            return;
        }
        //配置Activity下的所有module全限定名 后续可以根据名称还原module实体（实体才包含ViewGroup、Activity等参数）
        moduleConfig(new ArrayList<>(modules.keySet()));
        //依次给所有module初始化：1.创建实体 2.传递参数 3.调用初始化 4.纳入生命周期管理
        for (String moduleName : modules.keySet()) {
            //创建对应module
            AbsModule module = ModuleFactory.newModuleInstance(moduleName);
            if (module != null) {
                //创建参数
                ModuleContext moduleContext = new ModuleContext();
                moduleContext.setContext(activity);
                moduleContext.setSaveInstance(saveInstance);
                SparseArrayCompat<ViewGroup> viewGroups = new SparseArrayCompat<>();
                ArrayList<Integer> mViewIds = modules.get(moduleName);
                if (mViewIds != null && mViewIds.size() > 0) {
                    for (int i = 0; i < mViewIds.size(); i++) {
                        viewGroups.put(i, (ViewGroup) activity.findViewById(mViewIds.get(i)));
                    }
                }
                moduleContext.setViewGroups(viewGroups);
                //调用初始化（参数传递）
                module.init(moduleContext);
                //纳入管理
                allModules.put(moduleName, module);
            }
        }
    }
}