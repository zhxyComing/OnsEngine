package com.dixon.onsengine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 基于SharedPreferences的配置存储
 */

public class SharedConfig {

    private final static String OE_RUNNER = "oe_runner_config";

    private final static String IS_HIDE_UNKNOWN = "is_hide_unknown";
    private final static String IS_FULL_SCREEN = "is_full_screen";
    private final static String IS_SHOW_BOARD = "is_show_board";
    private final static String IS_DELETE_AFTER_UNZIP = "is_delete_after_unzip";
    private final static String IS_UPDATE_SHOW_PREFIX = "is_update_show_prefix_";

    private static SharedConfig mInstance;
    private static SharedPreferences mPreferences;
    private static Editor mEditor;

    public SharedConfig(Context context) {
        mPreferences = context.getApplicationContext().getSharedPreferences(OE_RUNNER, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new SharedConfig(context);
        }
    }

    public static SharedConfig Instance() {
        return mInstance;
    }

    public static SharedPreferences getPreferences() {
        return mPreferences;
    }


    public boolean setHideUnknow(boolean isHide) {
        mEditor.putBoolean(IS_HIDE_UNKNOWN, isHide);
        return mEditor.commit();
    }

    // 是否隐藏未知文件 默认否
    public boolean isHideUnknown() {
        return mPreferences.getBoolean(IS_HIDE_UNKNOWN, false);
    }

    public boolean setFullScreen(boolean isFullScreen) {
        mEditor.putBoolean(IS_FULL_SCREEN, isFullScreen);
        return mEditor.commit();
    }

    // 是否全屏显示 默认否
    public boolean isFullScreen() {
        return mPreferences.getBoolean(IS_FULL_SCREEN, false);
    }

    public boolean setShowBoard(boolean isHide) {
        mEditor.putBoolean(IS_SHOW_BOARD, isHide);
        return mEditor.commit();
    }

    // 是否显示虚拟键盘 模式是
    public boolean isShowBoard() {
        return mPreferences.getBoolean(IS_SHOW_BOARD, true);
    }

    public boolean setDeleteAfterUnZip(boolean isDelete) {
        mEditor.putBoolean(IS_DELETE_AFTER_UNZIP, isDelete);
        return mEditor.commit();
    }

    public boolean isDeleteAfterUnZip() {
        return mPreferences.getBoolean(IS_DELETE_AFTER_UNZIP, true);
    }

    public boolean setUpdateShown(String versionName) {
        mEditor.putBoolean(IS_UPDATE_SHOW_PREFIX + versionName, true);
        return mEditor.commit();
    }

    public boolean isUpdateShown(String versionName) {
        return mPreferences.getBoolean(IS_UPDATE_SHOW_PREFIX + versionName, false);
    }
}

