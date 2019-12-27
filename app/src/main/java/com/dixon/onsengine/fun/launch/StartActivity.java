package com.dixon.onsengine.fun.launch;

import android.content.Intent;
import android.os.Bundle;

import com.dixon.onsengine.R;
import com.dixon.onsengine.base.BaseActivity;
import com.dixon.onsengine.fun.home.HomeActivity;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            goToHome();
        }).start();
    }

    private void goToHome() {
        startActivity(new Intent(StartActivity.this, HomeActivity.class));
        finish();
    }
}
