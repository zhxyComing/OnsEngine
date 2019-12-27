package com.dixon.onsengine.fun.launch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dixon.onsengine.R;
import com.dixon.onsengine.base.BaseActivity;
import com.dixon.onsengine.core.util.DialogUtil;
import com.dixon.onsengine.core.util.Toast;

/**
 * 权限申请页
 */
public class LaunchActivity extends BaseActivity {

    private TextView mPermissionAskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mPermissionAskBtn.setOnClickListener(v -> runPermissionDetection());

        tryToHomePage();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mPermissionAskBtn = findViewById(R.id.al_tv_ask_permission);
    }

    private void tryToHomePage() {
        if (Build.VERSION.SDK_INT >= 23) {
            final String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        }
        goToStartPage();
    }

    private void goToStartPage() {
        startActivity(new Intent(LaunchActivity.this, StartActivity.class));
        finish();
    }

    /**
     * 申请读写权限
     *
     * @return
     */
    private boolean runPermissionDetection() {
        if (Build.VERSION.SDK_INT >= 23) {
            final String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    LaunchActivity.this.requestPermissions(permissions, 0);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showGuide();
        } else {
            Toast.show(LaunchActivity.this, "权限未获得");
        }
    }

    private final String guideDesc = "权限已获得，请将游戏文件或压缩包直接复制到 /storage/emulated/0/OERunner (即sd卡根目录/OERunner)文件夹下即可识别，点击 OK 进入首页。";

    private void showGuide() {
        DialogUtil.showGuideDialog(this, guideDesc, v -> goToStartPage());
    }
}
