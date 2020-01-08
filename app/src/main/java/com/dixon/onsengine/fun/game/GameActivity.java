package com.dixon.onsengine.fun.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dixon.onsengine.R;
import com.dixon.onsengine.SharedConfig;
import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.ScreenUtil;
import com.onscripter.ONScripterView;
import com.onscripter.exception.NativeONSException;

import java.io.File;

/**
 * 使用ONS引擎的Activity 即游戏页面
 * <p>
 * 引擎Github https://github.com/matthewn4444/onscripter-engine-android
 */
public class GameActivity extends Activity {

    public static GameActivity sCurrentGameActivity = null;

    private FrameLayout mGameContent;

    private static final String GAME_PATH = "game_path";

    private ONScripterView mGameView;

    // 暂时只增加俩个虚拟键
    private TextView mClickView, mBackView, mScreenShotView;
    private LinearLayout mBoardLayout, mBoardLayoutLeft;
    // 截图用
    private MediaProjectionManager mMediaProjectionManager;

    private static final int REQUEST_SCREEN_SHOT = 100;

    public static void startGame(Context context, String path) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(GAME_PATH, path);
        context.startActivity(intent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mGameContent = findViewById(R.id.ag_fl_content);
        mClickView = findViewById(R.id.ag_tv_click);
        mBackView = findViewById(R.id.ag_tv_back);
        mBoardLayout = findViewById(R.id.ag_ll_board_layout);
        mBoardLayoutLeft = findViewById(R.id.ag_ll_board_layout_left);
        mScreenShotView = findViewById(R.id.ag_tv_screen_shot);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        sCurrentGameActivity = this;

        String path = getIntent().getStringExtra(GAME_PATH);
        if (TextUtils.isEmpty(path)) {
            finish();
            return;
        }

        // Defined uri either content:// (external devices like sdcard) or file://
        final Uri uri = Uri.fromFile(new File(path));
        mGameView = new ONScripterView.Builder(this, uri)
                // If you specify a screenshot folder name, relative to the save folder in game,
                // full sized screenshots are saved after each save
                .setScreenshotPath(FileUtil.getSDPath() + "/OERunnerSetting/ScreenShot/")
                // Outline of text
                .useRenderOutline()
                // Plays higher quality audio
                .useHQAudio()
                // Set a default font path
//                .setFontPath(defaultFontPath)
                .create();
        // 自定义加的尺寸设置 有bug -_-!
        // 目前全屏会让按键错位 后续尝试修复
        if (SharedConfig.Instance().isFullScreen()) {
            mGameView.setSize(ScreenUtil.getDisplayWidth(this), ScreenUtil.getDisplayHeight(this));
        } else {
            // 虚拟键80dp 虚拟键外边距22dp 如果游戏尺寸+虚拟键尺寸*2+虚拟键外边距*4>屏幕尺寸 则不显示虚拟键
            int gameRealWidth = mGameView.getGameWidth() * ScreenUtil.getDisplayHeight(GameActivity.this) / mGameView.getGameHeight();
            int contentRealWidth = (int) (gameRealWidth + ScreenUtil.dpToPx(GameActivity.this, 80) * 2 + ScreenUtil.dpToPx(GameActivity.this, 22) * 4);
            if (contentRealWidth > ScreenUtil.getDisplayWidth(this)) {
                com.dixon.onsengine.core.util.Toast.show(this, "屏幕空间不足，虚拟键已隐藏");
            } else {
                addBoardView();
            }
        }
        mGameView.setKeepScreenOn(true);
        Log.e("GameActivity", "WH：" + mGameView.getGameWidth() + " " + mGameView.getGameHeight());
        mGameContent.addView(mGameView);

        // [Optional] Receive Events from the game
        mGameView.setONScripterEventListener(new ONScripterView.ONScripterEventListener() {
            @Override
            public void autoStateChanged(boolean selected) {
                // User has toggled auto mode
            }

            @Override
            public void skipStateChanged(boolean selected) {
                // User has toggled skip mode
            }

            @Override
            public void singlePageStateChanged(boolean selected) {
                // User has toggled single page mode
            }

            @Override
            public void videoRequested(Uri videoUri, boolean clickToSkip, boolean shouldLoop) {
                // Request playing this video in an external video player
                // If you have your own video player built into your app, you can
                // pause this thread and play the video. Unfortunately I was unable
                // to get smpeg library to work within this library
                // todo need test 1 不确定高版本有没有问题
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(uri, "video/*");
                    startActivityForResult(i, -1);
                } catch (Exception e) {
                    Log.e("ONScripter", "playVideo error:  " + e.getClass().getName());
                }
            }

            @Override
            public void onReady() {
                Log.e("ONScripter", "Game is ready");
                // 启动时直接跳转至存档1 后续再加
                // Load save file, save1.dat
//                 mGameView.loadSaveFile(1);
            }

            @Override
            public void onNativeError(NativeONSException e, String line, String backtrace) {
                Toast.makeText(GameActivity.this, "An error has occured: " + line, Toast.LENGTH_SHORT).show();
                Log.e("ONScripter", backtrace);
            }

            @Override
            public void onUserMessage(ONScripterView.UserMessage messageId) {
                if (messageId == ONScripterView.UserMessage.CORRUPT_SAVE_FILE) {
                    Toast.makeText(GameActivity.this, "Cannot open save file, it is corrupted",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGameFinished() {
                // Game ended
                finish();
            }
        });

        // Center the game in the middle of the screen
        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) mGameView.getLayoutParams();
        p.gravity = Gravity.CENTER;
//        p.width = FrameLayout.LayoutParams.MATCH_PARENT;
//        p.height = FrameLayout.LayoutParams.MATCH_PARENT;
        mGameView.setLayoutParams(p);

        // Set black background behind the engine
        findViewById(android.R.id.content).setBackgroundColor(Color.BLACK);
    }

    private void addBoardView() {
        if (!SharedConfig.Instance().isShowBoard()) {
            return;
        }
        mBoardLayout.setVisibility(View.VISIBLE);
        mBoardLayoutLeft.setVisibility(View.VISIBLE);
        mClickView.setOnClickListener(v -> {
            mGameView.sendNativeKeyPress(KeyEvent.KEYCODE_ENTER);
        });
        mBackView.setOnClickListener(v -> mGameView.sendNativeKeyPress(KeyEvent.KEYCODE_BACK));
        // 截图键
        mScreenShotView.setOnClickListener(v -> {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if (mMediaProjectionManager != null) {
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_SCREEN_SHOT);
            } else {
                com.dixon.onsengine.core.util.Toast.show(this, "异常，无法截图");
            }
        });
    }

    // 截图回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCREEN_SHOT) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "取消截图", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics metrics = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(metrics);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent service = new Intent(this, ScreenRecorder.class);
            service.putExtra("code", resultCode);
            service.putExtra("data", data);//intent
            service.putExtra("game_width", mGameView.getGameWidth());
            service.putExtra("game_height", mGameView.getGameHeight());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// 8.0？
                startForegroundService(service);
            } else {
                com.dixon.onsengine.core.util.Toast.show(this, "当前Android版本较低，暂不支持应用截图，请使用系统截图");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGameView != null) {
            mGameView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGameView != null) {
            mGameView.onResume();
        }

        // Set immersive mode
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        sCurrentGameActivity = null;
        // DO NOT EXIT APP HERE, DO IT BEFORE OR PREVIOUS ACTIVITY WILL FREEZE, will fix one day
        super.onDestroy();
    }

    public MediaProjectionManager getMediaProjectionManager() {
        return mMediaProjectionManager;
    }
}
