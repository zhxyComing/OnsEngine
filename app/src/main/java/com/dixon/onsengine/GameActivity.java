package com.dixon.onsengine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dixon.onsengine.core.util.FileUtil;
import com.onscripter.ONScripterView;
import com.onscripter.exception.NativeONSException;

import java.io.File;

/**
 * 使用ONS引擎的Activity 即游戏页面
 * <p>
 * 引擎Github https://github.com/matthewn4444/onscripter-engine-android
 */
public class GameActivity extends Activity {

    private static final String GAME_PATH = "game_path";

    private ONScripterView mGameView;

    public static void startGame(Context context, String path) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(GAME_PATH, path);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setContentView(mGameView);

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
                // todo need test 1
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
                // todo need test 2
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
        mGameView.setLayoutParams(p);

        // Set black background behind the engine
        findViewById(android.R.id.content).setBackgroundColor(Color.BLACK);
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
        // DO NOT EXIT APP HERE, DO IT BEFORE OR PREVIOUS ACTIVITY WILL FREEZE, will fix one day
        super.onDestroy();
    }
}
