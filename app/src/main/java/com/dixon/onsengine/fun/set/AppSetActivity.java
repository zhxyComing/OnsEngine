package com.dixon.onsengine.fun.set;

import android.os.Bundle;
import android.widget.Switch;

import com.dixon.onsengine.R;
import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.base.BaseActivity;
import com.dixon.onsengine.bean.event.HomeRefreshEvent;

import org.greenrobot.eventbus.EventBus;

public class AppSetActivity extends BaseActivity {

    private Switch mHideUnKnownView, mDeleteAfterUnZipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_set);

        initView();
    }

    private void initView() {
        setHideUnKnownView();
        setDeleteAfterUnZipView();
    }

    // todo 抽象为设置基类
    private void setDeleteAfterUnZipView() {
        boolean isDelete = SharedConfig.Instance().isDeleteAfterUnZip();
        if (isDelete) {
            mDeleteAfterUnZipView.setChecked(true);
        } else {
            mDeleteAfterUnZipView.setChecked(false);
        }
        mDeleteAfterUnZipView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedConfig.Instance().setDeleteAfterUnZip(isChecked);
        });
    }

    private void setHideUnKnownView() {
        boolean isHide = SharedConfig.Instance().isHideUnknown();
        if (isHide) {
            mHideUnKnownView.setChecked(true);
        } else {
            mHideUnKnownView.setChecked(false);
        }
        mHideUnKnownView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedConfig.Instance().setHideUnknow(isChecked);
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mHideUnKnownView = findViewById(R.id.aas_sw_hide_unknown);
        mDeleteAfterUnZipView = findViewById(R.id.aas_sw_delete_after_unzip);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new HomeRefreshEvent());
    }
}
