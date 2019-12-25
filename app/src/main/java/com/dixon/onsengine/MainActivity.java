package com.dixon.onsengine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.onscripter.ONScripterView;
import com.onscripter.exception.NativeONSException;

import java.io.File;

public class MainActivity extends Activity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }

    private ONScripterView mGame;

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("testkkk", "1");
        // Defined uri either content:// (external devices like sdcard) or file://
//        final Uri uri = Uri.fromFile(new File("/path/to/folder/with/game"));
//        final Uri uri = createUrl(new File(getSDPath() + "/game"));
//        Log.e("testkkk", "path " + getPath(this, uri));

        final Uri uri = Uri.fromFile(new File(getSDPath() + "/game"));

        mGame = new ONScripterView.Builder(this, uri)
                // If you specify a screenshot folder name, relative to the save folder in game,
                // full sized screenshots are saved after each save
                .setScreenshotPath("")
                // Outline of text
                .useRenderOutline()
                // Plays higher quality audio
                .useHQAudio()
                // Set a default font path
//                .setFontPath(defaultFontPath)
                .create();
        setContentView(mGame);

        Log.e("testkkk", "3");

        // [Optional] Receive Events from the game
        mGame.setONScripterEventListener(new ONScripterView.ONScripterEventListener() {
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
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(uri, "video/*");
                    startActivityForResult(i, -1);
                } catch (Exception e) {
                    Log.e("ONScripter", "playVideo error:  " + e.getClass().getName());
                }
            }

//            @Override
//            public void videoRequested(String filename, boolean clickToSkip, boolean shouldLoop) {
//                // Request playing this video in an external video player
//                // If you have your own video player built into your app, you can
//                // pause this thread and play the video. Unfortunately I was unable
//                // to get smpeg library to work within this library
//                try {
//                    String filename2 = filename.replace('\\', '/');
//                    Uri uri = Uri.parse(filename2);
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setDataAndType(uri, "video/*");
//                    startActivityForResult(i, -1);
//                }
//                catch(Exception e){
//                    Log.e("ONScripter", "playVideo error:  " + e.getClass().getName());
//                }
//            }

            @Override
            public void onReady() {
                Log.w("ONScripter", "Game is ready");
                // Load save file, save1.dat
                // mGame.loadSaveFile(1);
            }

            @Override
            public void onNativeError(NativeONSException e, String line, String backtrace) {
                Toast.makeText(MainActivity.this, "An error has occured: " + line, Toast.LENGTH_SHORT).show();
                Log.w("ONScripter", backtrace);
            }

            @Override
            public void onUserMessage(ONScripterView.UserMessage messageId) {
                if (messageId == ONScripterView.UserMessage.CORRUPT_SAVE_FILE) {
                    Toast.makeText(MainActivity.this, "Cannot open save file, it is corrupted",
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
        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) mGame.getLayoutParams();
        p.gravity = Gravity.CENTER;
        mGame.setLayoutParams(p);

        // Set black background behind the engine
        findViewById(android.R.id.content).setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGame != null) {
            mGame.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGame != null) {
            mGame.onResume();
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
//        if (mGame != null) {
//            mGame.onStop();
//        }
    }

    @Override
    public void onDestroy() {
        // DO NOT EXIT APP HERE, DO IT BEFORE OR PREVIOUS ACTIVITY WILL FREEZE, will fix one day
        super.onDestroy();
    }
}
