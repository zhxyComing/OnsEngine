package com.dixon.onsengine.core.module;

import android.text.TextUtils;

/**
 * module 实体反射类
 */
public class ModuleFactory {

    //反射初始化对应module
    public static AbsModule newModuleInstance(String moduleName) {
        if (TextUtils.isEmpty(moduleName)) {
            return null;
        }
        try {
            Class<? extends AbsModule> moduleClzz = (Class<? extends AbsModule>) Class.forName(moduleName);
            return moduleClzz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}