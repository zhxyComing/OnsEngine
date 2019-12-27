package com.dixon.onsengine.fun.set;

import android.os.Bundle;
import android.widget.Switch;

import com.dixon.onsengine.R;
import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.base.BaseActivity;

public class OnsSetActivity extends BaseActivity {

    private Switch mFullScreenSetView, mBoardSetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ons_set);

        initView();
    }

    private void initView() {
        setFullScreenSetView();
        setBoardSetView();
    }

    private void setBoardSetView() {
        boolean isShowBoard = SharedConfig.Instance().isShowBoard();
        if (isShowBoard) {
            mBoardSetView.setChecked(true);
        } else {
            mBoardSetView.setChecked(false);
        }
        mBoardSetView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedConfig.Instance().setShowBoard(isChecked);
        });
    }

    private void setFullScreenSetView() {
        boolean isFull = SharedConfig.Instance().isFullScreen();
        if (isFull) {
            mFullScreenSetView.setChecked(true);
        } else {
            mFullScreenSetView.setChecked(false);
        }
        mFullScreenSetView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedConfig.Instance().setFullScreen(isChecked);
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mFullScreenSetView = findViewById(R.id.aos_sw_full_screen);
        mBoardSetView = findViewById(R.id.aos_sw_abs_btn);
    }
}
