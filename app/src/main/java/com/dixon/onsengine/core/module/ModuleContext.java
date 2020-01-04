package com.dixon.onsengine.core.module;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.collection.SparseArrayCompat;

/**
 * 子模块需要注入的参数
 */
public class ModuleContext {
    private Activity context; //上下文对象
    private Bundle saveInstance; //保存状态的对象
    private SparseArrayCompat<ViewGroup> viewGroups = new SparseArrayCompat<>();

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public Bundle getSaveInstance() {
        return saveInstance;
    }

    public void setSaveInstance(Bundle saveInstance) {
        this.saveInstance = saveInstance;
    }

    public SparseArrayCompat<ViewGroup> getViewGroups() {
        return viewGroups;
    }

    public void setViewGroups(SparseArrayCompat<ViewGroup> viewGroups) {
        this.viewGroups = viewGroups;
    }
}