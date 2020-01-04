package com.dixon.onsengine.core.module.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

public class ModulePassInOrOutManager {

    private View mInLayout;
    private View mCurrentLayout;

    private boolean isRunning = false;

    public View getInLayout() {
        return mInLayout;
    }

    public void setInLayout(View mInLayout) {
        this.mInLayout = mInLayout;
    }

    public void init(View currentLayout) {
        this.mCurrentLayout = currentLayout;
    }

    public void animRun(OnBeforeAnimationRunningListener beforeRunningListener) {
        if (isRunning || mInLayout == mCurrentLayout) {
            return;
        }
        isRunning = true;
        //setTextSize
        beforeRunningListener.beforeRunning();

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mCurrentLayout, "alpha", 1f, 0f);
        fadeOut.setDuration(150);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mInLayout, "alpha", 0f, 1f);
        fadeIn.setDuration(150);

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mInLayout.setVisibility(View.VISIBLE);
                mCurrentLayout.setVisibility(View.GONE);
                fadeIn.start();
            }
        });

        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isRunning = false;
                mCurrentLayout = mInLayout;
            }
        });

        fadeOut.start();
    }

    public interface OnBeforeAnimationRunningListener {
        void beforeRunning();
    }
}
