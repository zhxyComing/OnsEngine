package com.dixon.onsengine.fun;

import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.base.BaseApplication;

public class OERunnerApplication extends BaseApplication {
    @Override
    protected void onInitSpeed() {

    }

    @Override
    protected void onInitLow() {
        SharedConfig.init(this);
    }

    @Override
    protected void onFirstInApp() {

    }
}
