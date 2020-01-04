package com.dixon.onsengine.core.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 普通的截图针对 SurfaceView 是不可行的...
 */
public class ScreenShotUtil {

    /**
     * 屏幕截图
     *
     * @param activity
     * @return
     */
    public static Bitmap screenShot(Activity activity, String filePath) {
        if (activity == null) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists() && !file.mkdirs()) {
            return null;
        }
        View view = activity.getWindow().getDecorView();
        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        //获取屏幕宽和高
        int width = ScreenUtil.getDisplayWidth(view.getContext());
        int height = ScreenUtil.getDisplayHeight(view.getContext());

        // 全屏不用考虑状态栏，有导航栏需要加上导航栏高度
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                    height);
        } catch (Exception e) {
            // 这里主要是为了兼容异形屏做的处理，我这里的处理比较仓促，直接靠捕获异常处理
            // 其实vivo oppo等这些异形屏手机官网都有判断方法
            // 正确的做法应该是判断当前手机是否是异形屏，如果是就用下面的代码创建bitmap


            String msg = e.getMessage();
            // 部分手机导航栏高度不占窗口高度，不用添加，比如OppoR15这种异形屏
            if (msg.contains("<= bitmap.height()")) {
                try {
                    bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                            height);
                } catch (Exception e1) {
                    msg = e1.getMessage();
                    // 适配Vivo X21异形屏，状态栏和导航栏都没有填充
                    if (msg.contains("<= bitmap.height()")) {
                        try {
                            bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                                    height - ScreenUtil.getStatusHeight(view.getContext()));
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        e1.printStackTrace();
                    }
                }
            } else {
                e.printStackTrace();
            }
        }

        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        if (null != bitmap) {
            try {
                compressAndGenImage(bitmap, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * view截图
     *
     * @return
     */
    public static void viewShot(@NonNull final View v, @Nullable final String filePath) {
        File file = new File(filePath);
        if (!file.exists() && !file.mkdirs()) {
            return;
        }
        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 核心代码start
                Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
                v.draw(c);
                // end
                try {
                    compressAndGenImage(bitmap, filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void compressAndGenImage(Bitmap image, String outPath) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 70;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(createImagePath(outPath));
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }

    private static final String IMAGE_FILE_NAME_TEMPLATE = "ScreenShot%s.jpg";
    private static final String IMAGE_FILE_PATH_TEMPLATE = "%s/%s";

    public static String createImagePath(String outPath) {
        //判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //文件名
            long systemTime = System.currentTimeMillis();
            String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA).format(new Date(systemTime));
            String mFileName = String.format(IMAGE_FILE_NAME_TEMPLATE, imageDate);

            //文件全名
            String filePath = String.format(IMAGE_FILE_PATH_TEMPLATE, outPath, mFileName);
            File file = new File(filePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return filePath;
        }
        return "";
    }
}
