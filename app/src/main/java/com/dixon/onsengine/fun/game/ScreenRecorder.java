package com.dixon.onsengine.fun.game;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dixon.onsengine.R;
import com.dixon.onsengine.base.BaseApplication;
import com.dixon.onsengine.base.IGetActivity;
import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.ScreenShotUtil;
import com.dixon.onsengine.core.util.ScreenUtil;
import com.dixon.onsengine.core.util.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ScreenRecorder extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (GameActivity.sCurrentGameActivity == null) {
            ScreenRecorder.this.stopSelf();
            Toast.show(this, "游戏已关闭，无法截图");
            return super.onStartCommand(intent, flags, startId);
        }
        createNotificationChannel();

        Intent mResultData = intent.getParcelableExtra("data");
        int mResultCode = intent.getIntExtra("code", -1);
        int gameWidth = intent.getIntExtra("game_width", 0);
        int gameHeight = intent.getIntExtra("game_height", 0);

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager == null) {
            ScreenRecorder.this.stopSelf();
            Toast.show(this, "无法获取系统服务，截图失败");
            return super.onStartCommand(intent, flags, startId);
        }
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(mResultCode, Objects.requireNonNull(mResultData));
        ImageReader mImageReader = ImageReader.newInstance(ScreenUtil.getDisplayWidth(this), ScreenUtil.getDisplayHeight(this), 0x1, 2);
        mediaProjection.createVirtualDisplay("ScreenCapture",
                ScreenUtil.getDisplayWidth(this), ScreenUtil.getDisplayHeight(this), getResources().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
        // 延迟截图
        new Handler().postDelayed(() -> {
            Image image = null;
            image = mImageReader.acquireLatestImage();
            if (image == null) {
                return;
            }
            int width = 0;
            width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            // 真正的游戏内容截图
            int imageRealWidth = gameWidth * ScreenUtil.getDisplayHeight(ScreenRecorder.this) / gameHeight;
            bitmap = Bitmap.createBitmap(bitmap,
                    (ScreenUtil.getDisplayWidth(ScreenRecorder.this) - imageRealWidth) / 2 + ScreenUtil.getStatusHeight(ScreenRecorder.this) / 2,
                    0, imageRealWidth, height);
            image.close();

            if (bitmap != null) {
                // 将bitmap保存到本地
                try {
                    ScreenShotUtil.compressAndGenImage(bitmap, FileUtil.getSDPath() + "/OERunnerSetting/ScreenShot");
                } catch (IOException e) {
                    Toast.show(ScreenRecorder.this, "图片保存失败 " + e.toString());
                } finally {
                    bitmap.recycle();
                }
            }
            Toast.show(ScreenRecorder.this, "截图保存至/OERunnerSetting/ScreenShot目录");
            ScreenRecorder.this.stopSelf();
        }, 500);

        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, GameActivity.class); //点击后跳转的界面，可以设置跳转数据

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.app_icon)) // 设置下拉列表中的图标(大图标)
                //.setContentTitle("SMI InstantView") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.app_icon) // 设置状态栏内的小图标
                .setContentText("is running......") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        /*以下是对Android 8.0的适配*/
        //普通notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        //前台服务notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);

    }
}
