package com.dixon.onsengine.zip;

import android.text.TextUtils;
import android.util.Log;

import com.dixon.onsengine.core.Service;
import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.HandlerUtil;
import com.leo618.zip.IZipCallback;
import com.leo618.zip.ZipManager;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUnZip implements IUnZipExecutor {

    @Override
    public void unZip(File file, String saveDirectory, IUnZipCallback callback) {
        Service.getSingleService().execute(() -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                // 进度条更加顺滑
                unZipFolder(file.getPath(), saveDirectory, callback);
            } else {
                ZipManager.unzip(file.getPath(), saveDirectory, new IZipCallback() {
                    @Override
                    public void onStart() {
                        HandlerUtil.runOnUiThread(callback::onStart);
                    }

                    @Override
                    public void onProgress(int percentDone) {
                        HandlerUtil.runOnUiThread(() -> callback.onProcess(percentDone));
                    }

                    @Override
                    public void onFinish(boolean success) {
                        if (success) {
                            HandlerUtil.runOnUiThread(callback::onSucceed);
                        } else {
                            HandlerUtil.runOnUiThread(() -> callback.onError("未知错误"));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void unZipWithSecret(File file, String saveDirectory, IUnZipCallback callback, String password) {
        String dir = "";
        try {
            // 默认压缩包内只有一个根目录文件夹 多个的情况不是游戏暂不考虑 毕竟不是解压缩软件...
            ZipFile zipFile = new ZipFile(file);
            List fileHeaders = zipFile.getFileHeaders();
            if (fileHeaders != null && fileHeaders.size() > 0) {
                String allPath = ((FileHeader) fileHeaders.get(0)).getFileName();
                if (allPath.contains("/")) {
                    String[] array = allPath.split("/");
                    dir = array[0];
                } else {
                    dir = allPath;
                }
                Log.e("ZipUnZip", "dir is " + dir);
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        String finalDir = dir;
        ZipManager.unzip(file.getPath(), saveDirectory, password, new IZipCallback() {
            @Override
            public void onStart() {
                HandlerUtil.runOnUiThread(callback::onStart);
            }

            @Override
            public void onProgress(int percentDone) {
                Log.e("ZipUnZip", "progress");
                HandlerUtil.runOnUiThread(() -> callback.onProcess(percentDone));
            }

            @Override
            public void onFinish(boolean success) {
                Log.e("ZipUnZip", "finish");
                if (success) {
                    // 即使密码错误 也会返回成功 所以这里判断解压完的文件是否是空文件 空文件则返回失败 检测方式有限 最有效的办法还是改源码...
                    if (!TextUtils.isEmpty(finalDir)) {
                        File res = new File(saveDirectory + File.separator + finalDir);
                        Log.e("ZipUnZip", "File is " + res.getPath());
                        if (res.exists() && FileUtil.getFolderSize(res) <= 0) {
                            //数据异常 结果文件夹为空 说明密码错误
                            FileUtil.deleteFile(res);
                            HandlerUtil.runOnUiThread(() -> callback.onError("密码错误"));
                        } else {
                            HandlerUtil.runOnUiThread(callback::onSucceed);
                        }
                    } else {
                        HandlerUtil.runOnUiThread(callback::onSucceed);
                    }
                } else {
                    HandlerUtil.runOnUiThread(() -> callback.onError("未知错误"));
                }
            }
        });
    }

    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString ZIP的名称
     * @param outPathString 要解压缩路径
     * @throws Exception
     */
    public static void unZipFolder(String zipFileString, String outPathString, IUnZipCallback callback) {
        //7.0及以上使用 不存在乱码问题
        ZipInputStream inZip = null;
        FileOutputStream out = null;
        int k = 0;
        long sum = 0;
        long lastTime = System.currentTimeMillis();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            HandlerUtil.runOnUiThread(callback::onStart);
            try {
                inZip = new ZipInputStream(new FileInputStream(zipFileString), Charset.forName("GBK"));
                ZipEntry zipEntry;
                String szName = "";
                while ((zipEntry = inZip.getNextEntry()) != null) {
                    szName = zipEntry.getName();
                    //完善文件夹名字
                    File srcFile = new File(zipFileString);
                    String realName;
                    if (srcFile.getName().contains(".")) {
                        String[] split = srcFile.getName().split("\\.");
                        realName = split[0];
                    } else {
                        realName = srcFile.getName();
                    }
                    Log.e("UnZip", "realName " + realName);
                    if (!szName.contains(realName)) {
                        szName = realName + File.separator + szName;
                    }
                    Log.e("UnZip", "szName " + szName);
                    if (zipEntry.isDirectory()) {
                        //获取部件的文件夹名
                        szName = szName.substring(0, szName.length() - 1);
                        File folder = new File(outPathString + File.separator + szName);
                        folder.mkdirs();
                    } else {
                        File file = new File(outPathString + File.separator + szName);
                        if (!file.exists()) {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                        }
                        // 获取文件的输出流
                        out = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        // 读取（字节）字节到缓冲区
                        while ((len = inZip.read(buffer)) != -1) {
                            // 从缓冲区（0）位置写入（字节）字节
                            out.write(buffer, 0, len);
                            out.flush();
                            sum += len;
                            // 加个日志就流畅了 见了鬼 怀疑和代码优化有关
                            // 1s回调一次
                            if (System.currentTimeMillis() - lastTime > 1000) {
                                long finalSum = sum;
                                HandlerUtil.runOnUiThread(() -> callback.onProgress(file.getName(), finalSum));
                                lastTime = System.currentTimeMillis();
                                Log.e("UnZip", "finalSum " + sum);
                            }
                        }
                        out.close();
                    }
                }
                inZip.close();
                HandlerUtil.runOnUiThread(callback::onSucceed);
            } catch (Exception e) {
                Log.e("UnZip", "zip解压错误：" + e.toString());
                HandlerUtil.runOnUiThread(() -> callback.onError(e.toString()));
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inZip != null) {
                    try {
                        inZip.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
