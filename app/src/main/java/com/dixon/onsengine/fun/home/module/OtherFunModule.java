package com.dixon.onsengine.fun.home.module;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dixon.onsengine.R;
import com.dixon.onsengine.core.module.AbsModule;
import com.dixon.onsengine.core.module.ModuleContext;
import com.dixon.onsengine.fun.home.HomeActivity;
import com.dixon.onsengine.fun.photo.PhotoDisplayActivity;

public class OtherFunModule extends AbsModule {

    private Activity activity;
    private ViewGroup parentViewGroup;

    private LinearLayout mPhotoLayout;

    @Override
    public void init(ModuleContext moduleContext) {
        activity = moduleContext.getContext();
        parentViewGroup = moduleContext.getViewGroups().get(0);
        initView();
    }

    private void initView() {
        LayoutInflater.from(activity).inflate(R.layout.module_home_other_fun, parentViewGroup, true);
        findView();
        initClick();
    }

    private void initClick() {
        mPhotoLayout.setOnClickListener(v -> activity.startActivity(new Intent(activity, PhotoDisplayActivity.class)));
    }

    private void findView() {
        mPhotoLayout = parentViewGroup.findViewById(R.id.mhof_ll_photo);
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
