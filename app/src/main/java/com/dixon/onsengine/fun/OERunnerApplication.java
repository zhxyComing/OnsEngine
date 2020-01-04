package com.dixon.onsengine.fun;

import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.base.BaseApplication;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class OERunnerApplication extends BaseApplication {
    @Override
    protected void onInitSpeed() {

    }

    @Override
    protected void onInitLow() {
        SharedConfig.init(this);
        // 友盟 todo 后续抽离
        UMConfigure.init(this, "5e101721570df3b4380000a6", null, UMConfigure.DEVICE_TYPE_PHONE, null);
        // 无需在onResume onPause中插入代码
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    @Override
    protected void onFirstInApp() {

    }
}
