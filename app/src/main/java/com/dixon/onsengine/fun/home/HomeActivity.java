package com.dixon.onsengine.fun.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dixon.onsengine.BuildConfig;
import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.core.module.ModuleManagerActivity;
import com.dixon.onsengine.core.module.PageConfig;
import com.dixon.onsengine.core.module.anim.ModulePassInOrOutManager;
import com.dixon.onsengine.core.util.AnimationUtils;
import com.dixon.onsengine.core.util.ScreenUtil;
import com.dixon.onsengine.core.util.TimerUtils;
import com.dixon.onsengine.core.view.ToastView;
import com.dixon.onsengine.R;
import com.dixon.onsengine.core.monitor.AppStateRegister;
import com.dixon.onsengine.core.util.DialogUtil;
import com.dixon.onsengine.core.util.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends ModuleManagerActivity {

    private FrameLayout mAppStoreLayout, mOtherFunLayout, mSetLayout; //应用集 百宝盒 设置页
    private ModulePassInOrOutManager mModulePassInOrOutManager;//页面切换动画管理

    private TextView mAppTab, mOtherTab, mSetTab;

    //退出二次提示btn
    private ToastView mToastView;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AppStateRegister.register(this, this);
        initView();
        showUpdateMessage();
    }

    @Override
    public HashMap<String, ArrayList<Integer>> moduleConfig() {
        HashMap<String, ArrayList<Integer>> map = new HashMap<>();
        map.put(PageConfig.MODULE_HOME_APP_STORE, new ArrayList<Integer>() {{
            add(R.id.ah_app_store_layout);
        }});
        map.put(PageConfig.MODULE_HOME_SET, new ArrayList<Integer>() {{
            add(R.id.ah_set_layout);
        }});
        map.put(PageConfig.MODULE_HOME_OTHER_FUN, new ArrayList<Integer>() {{
            add(R.id.ah_other_fun_layout);
        }});
        return map;
    }

    /**
     * 显示更新信息弹窗 每版本只显示一次
     */
    private void showUpdateMessage() {
        if (!SharedConfig.Instance().isUpdateShown(BuildConfig.VERSION_NAME)) {
            SharedConfig.Instance().setUpdateShown(BuildConfig.VERSION_NAME);
            TimerUtils.mainDelay(1000, this, () ->
                    DialogUtil.showTipDialog(HomeActivity.this,
                            "更新内容",
                            FileUtil.getFromAssets("update.txt", HomeActivity.this),
                            v -> {
                            }));
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mAppStoreLayout = findViewById(R.id.ah_app_store_layout);
        mOtherFunLayout = findViewById(R.id.ah_other_fun_layout);
        mSetLayout = findViewById(R.id.ah_set_layout);
        mAppTab = findViewById(R.id.ah_tv_app);
        mOtherTab = findViewById(R.id.ah_tv_other);
        mSetTab = findViewById(R.id.ah_tv_set);
        mToastView = findViewById(R.id.ah_tv_exit);
    }

    private void initView() {
        mAppTab.setOnClickListener(v -> openAppTab());
        mSetTab.setOnClickListener(v -> openSetTab());
        mOtherTab.setOnClickListener(v -> openOtherTab());
        initToastCard();
        mModulePassInOrOutManager = new ModulePassInOrOutManager();
        mModulePassInOrOutManager.init(mAppStoreLayout);
    }

    // todo 抽离 Menu 优化代码
    private void openSetTab() {
        mModulePassInOrOutManager.setInLayout(mSetLayout);
        mModulePassInOrOutManager.animRun(() -> {
            //setTextSize
            mAppTab.setTextSize(16);
            mAppTab.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mAppTab.setTextColor(getResources().getColor(R.color.md_grey_600));
            mOtherTab.setTextSize(16);
            mOtherTab.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mOtherTab.setTextColor(getResources().getColor(R.color.md_grey_600));
            mSetTab.setTextSize(24);
            mSetTab.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mSetTab.setTextColor(getResources().getColor(R.color.md_grey_900));
        });
    }

    private void openAppTab() {
        mModulePassInOrOutManager.setInLayout(mAppStoreLayout);
        mModulePassInOrOutManager.animRun(() -> {
            //setTextSize
            mAppTab.setTextSize(24);
            mAppTab.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mAppTab.setTextColor(getResources().getColor(R.color.md_grey_900));
            mOtherTab.setTextSize(16);
            mOtherTab.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mOtherTab.setTextColor(getResources().getColor(R.color.md_grey_600));
            mSetTab.setTextSize(16);
            mSetTab.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mSetTab.setTextColor(getResources().getColor(R.color.md_grey_600));
        });
    }

    private void openOtherTab() {
        mModulePassInOrOutManager.setInLayout(mOtherFunLayout);
        mModulePassInOrOutManager.animRun(() -> {
            //setTextSize
            mAppTab.setTextSize(16);
            mAppTab.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mAppTab.setTextColor(getResources().getColor(R.color.md_grey_600));
            mOtherTab.setTextSize(24);
            mOtherTab.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mOtherTab.setTextColor(getResources().getColor(R.color.md_grey_900));
            mSetTab.setTextSize(16);
            mSetTab.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mSetTab.setTextColor(getResources().getColor(R.color.md_grey_600));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppStateRegister.unRegister(this);
    }

    @Override
    public void onBackPressed() {
        //与上次点击返回键时刻作差
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            mToastView.show("再按一次退出程序", 2000);
            mExitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    private void initToastCard() {
        mToastView.setVisibility(View.GONE);
        mToastView.setToastAnimEvent(new ToastView.ToastAnimEvent() {
            @Override
            public void show(long time) {
                mToastView.setVisibility(View.VISIBLE);
                AnimationUtils.tranX(mToastView,
                        ScreenUtil.dpToPx(HomeActivity.this, -200),
                        0, 300, new DecelerateInterpolator(), null).start();
            }

            @Override
            public void hide(long time) {
                AnimationUtils.tranX(mToastView, 0,
                        ScreenUtil.dpToPx(HomeActivity.this, -200), 300,
                        new DecelerateInterpolator(),
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mToastView.setVisibility(View.GONE);
                            }
                        }).start();
            }
        });
    }
}
