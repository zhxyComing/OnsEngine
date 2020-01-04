package com.dixon.onsengine.base;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dixon.onsengine.core.util.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
    }

    private void setStatusBar() {
        StatusBarUtil.setColorForStatus(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}