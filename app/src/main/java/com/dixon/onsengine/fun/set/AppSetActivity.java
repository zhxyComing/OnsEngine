package com.dixon.onsengine.fun.set;

import android.os.Bundle;
import android.widget.Switch;

import com.dixon.onsengine.R;
import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.bean.event.HomeRefreshEvent;

import org.greenrobot.eventbus.EventBus;

public class AppSetActivity extends BaseSetActivity {

    private Switch mHideUnKnownView, mHideUnKnownDirView, mSortAsTypeView, mDeleteAfterUnZipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_set);

        initView();
    }

    private void initView() {
        // 解压后删除源文件
        configSetItem(SharedConfig.Instance().isDeleteAfterUnZip(),
                mDeleteAfterUnZipView,
                (buttonView, isChecked) -> SharedConfig.Instance().setDeleteAfterUnZip(isChecked));
        // 隐藏未知文件
        configSetItem(SharedConfig.Instance().isHideUnknown(),
                mHideUnKnownView,
                (buttonView, isChecked) -> SharedConfig.Instance().setHideUnknow(isChecked));
        // App按照文件类型排序
        configSetItem(SharedConfig.Instance().isAppSortAsType(),
                mSortAsTypeView,
                (buttonView, isChecked) -> SharedConfig.Instance().setAppSortAsType(isChecked));
        //隐藏未知文件夹
        configSetItem(SharedConfig.Instance().isHideUnknownDir(),
                mHideUnKnownDirView,
                (buttonView, isChecked) -> SharedConfig.Instance().setHideUnknowDir(isChecked));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mHideUnKnownView = findViewById(R.id.aas_sw_hide_unknown);
        mHideUnKnownDirView = findViewById(R.id.aas_sw_hide_dir_unknown);
        mDeleteAfterUnZipView = findViewById(R.id.aas_sw_delete_after_unzip);
        mSortAsTypeView = findViewById(R.id.aas_sw_sort);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new HomeRefreshEvent());
    }
}
