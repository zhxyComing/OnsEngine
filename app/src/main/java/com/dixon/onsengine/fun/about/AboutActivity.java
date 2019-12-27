package com.dixon.onsengine.fun.about;

import android.os.Bundle;
import android.widget.TextView;

import com.dixon.onsengine.R;
import com.dixon.onsengine.base.BaseActivity;
import com.dixon.onsengine.core.util.FileUtil;

public class AboutActivity extends BaseActivity {

    private TextView mHelpView, mProblemView, mMessageView, mBackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initData();
    }

    private void initData() {
        mHelpView.setText(FileUtil.getFromAssets("help.txt", this));
        mProblemView.setText(FileUtil.getFromAssets("problem.txt", this));
        mMessageView.setText(FileUtil.getFromAssets("message.txt", this));
        mBackView.setText(FileUtil.getFromAssets("back.txt", this));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mHelpView = findViewById(R.id.aa_tv_help);
        mProblemView = findViewById(R.id.aa_tv_problem);
        mMessageView = findViewById(R.id.aa_tv_message);
        mBackView = findViewById(R.id.aa_tv_back);
    }
}
