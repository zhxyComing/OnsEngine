package com.dixon.onsengine.fun.set;

import android.widget.CompoundButton;
import android.widget.Switch;

import com.dixon.onsengine.base.BaseActivity;

public abstract class BaseSetActivity extends BaseActivity {

    protected void configSetItem(boolean tag, Switch sw, CompoundButton.OnCheckedChangeListener listener) {
        if (tag) {
            sw.setChecked(true);
        } else {
            sw.setChecked(false);
        }
        sw.setOnCheckedChangeListener(listener);
    }
}
