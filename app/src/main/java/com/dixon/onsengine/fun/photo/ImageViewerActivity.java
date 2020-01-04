package com.dixon.onsengine.fun.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dixon.onsengine.R;

import java.io.File;

public class ImageViewerActivity extends Activity {

    private ImageView mViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        hideBottomUIMenu();
        setContentView(R.layout.activity_image_viewer);

        String filePath = getIntent().getStringExtra("path");
        Glide.with(this)
                .load(Uri.fromFile(new File(filePath)))
                .into(mViewer);
    }

    public static void displayImage(Context context, String filePath) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra("path", filePath);
        context.startActivity(intent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewer = findViewById(R.id.aiv_iv_viewer);
    }

    // 隐藏顶部状态栏
    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //状态栏 color
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
    }

    // 隐藏底部虚拟键
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
