package com.dixon.onsengine.fun.home.module;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dixon.onsengine.R;
import com.dixon.onsengine.core.module.AbsModule;
import com.dixon.onsengine.core.module.ModuleContext;
import com.dixon.onsengine.fun.about.AboutActivity;
import com.dixon.onsengine.fun.set.AppSetActivity;
import com.dixon.onsengine.fun.set.OnsSetActivity;

public class SetModule extends AbsModule {

    private LinearLayout mAboutTab, mAppSetTab, mOnsSetTab;

    private Activity activity;
    private ViewGroup parentViewGroup;

    @Override
    public void init(ModuleContext moduleContext) {
        activity = moduleContext.getContext();
        parentViewGroup = moduleContext.getViewGroups().get(0);
        initView();
    }

    private void initView() {
        LayoutInflater.from(activity).inflate(R.layout.module_home_set, parentViewGroup, true);
        findView();
        initClick();
    }

    private void initClick() {
        mAboutTab.setOnClickListener(v -> activity.startActivity(new Intent(activity, AboutActivity.class)));
        mAppSetTab.setOnClickListener(v -> activity.startActivity(new Intent(activity, AppSetActivity.class)));
        mOnsSetTab.setOnClickListener(v -> activity.startActivity(new Intent(activity, OnsSetActivity.class)));
    }

    private void findView() {
        mOnsSetTab = parentViewGroup.findViewById(R.id.ah_ll_ons_set);
        mAboutTab = parentViewGroup.findViewById(R.id.ah_ll_about_tab);
        mAppSetTab = parentViewGroup.findViewById(R.id.ah_ll_app_set_tab);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onOrientationChanges(boolean isLandscape) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void appTurnIntoForeground() {

    }

    @Override
    public void appTurnIntoBackGround() {

    }
}
