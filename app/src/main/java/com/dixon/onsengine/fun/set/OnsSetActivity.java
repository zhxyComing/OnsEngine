package com.dixon.onsengine.fun.set;

import android.os.Bundle;
import android.widget.Switch;

import com.dixon.onsengine.R;
import com.dixon.onsengine.SharedConfig;

public class OnsSetActivity extends BaseSetActivity {

    private Switch mFullScreenSetView, mBoardSetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ons_set);

        initView();
    }

    private void initView() {
        // 是否显示虚拟键
        configSetItem(SharedConfig.Instance().isShowBoard(),
                mBoardSetView,
                (buttonView, isChecked) -> SharedConfig.Instance().setShowBoard(isChecked));

        // 是否全屏
        configSetItem(SharedConfig.Instance().isFullScreen(),
                mFullScreenSetView,
                (buttonView, isChecked) -> SharedConfig.Instance().setFullScreen(isChecked));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mFullScreenSetView = findViewById(R.id.aos_sw_full_screen);
        mBoardSetView = findViewById(R.id.aos_sw_abs_btn);
    }
}
