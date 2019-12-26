package com.dixon.onsengine.core.util;

import android.util.Log;

import com.dixon.onsengine.bean.IUnZipCallback;
import com.dixon.onsengine.bean.IZipType;
import com.dixon.onsengine.core.Params;
import com.dixon.onsengine.core.Service;
import com.hzy.lib7z.IExtractCallback;
import com.hzy.lib7z.Z7Extractor;
import com.leo618.zip.IZipCallback;
import com.leo618.zip.ZipManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

/**
 * 解压缩工具类
 * <p>
 * todo 代码优化
 */
public class UnZipUtil {

    public static void unZip(File file, int Type, IUnZipCallback callback) {
        switch (Type) {
            case IZipType.SevenZ:
                unZipSevenZ(file, callback);
                break;
            case IZipType.ZIP:
                unZipZip(file, callback);
                break;
            case IZipType.RAR:
                unZipRar(file, callback);
                break;
            default:
                callback.onError("不支持的解压格式");
        }
    }

    private static void unZipRar(File srcFile, IUnZipCallback callback) {
        Service.getSingleService().execute(() -> unZipRar(srcFile, Params.getGameDirectory(), callback));
    }


    private static void unZipRar(File srcFile, String unrarPath, IUnZipCallback callback) {
        if (null == unrarPath || "".equals(unrarPath)) {
            unrarPath = srcFile.getParentFile().getPath();
        }
        // 保证文件夹路径最后是"/"或者"\"
        char lastChar = unrarPath.charAt(unrarPath.length() - 1);
        if (lastChar != '/' && lastChar != '\\') {
            unrarPath += File.separator;
        }
        String realName;
        if (srcFile.getName().contains(".")) {
            String[] split = srcFile.getName().split("\\.");
            realName = split[0];
        } else {
            realName = srcFile.getName();
        }
        unrarPath = unrarPath + realName + File.separator;

        if (callback != null) {
            HandlerUtil.runOnUiThread(callback::onStart);
        }

        FileOutputStream fileOut = null;
        Archive rarfile = null;

        try {
            rarfile = new Archive(srcFile);
            FileHeader fh = null;
            long sum = 0;
            for (int i = 0; i < rarfile.getFileHeaders().size(); i++) {
                fh = rarfile.getFileHeaders().get(i);
                String entrypath = "";
                if (fh.isUnicode()) {//解決中文乱码
                    entrypath = fh.getFileNameW().trim();
                } else {
                    entrypath = fh.getFileNameString().trim();
                }
                entrypath = entrypath.replaceAll("\\\\", "/");
                File file = new File(unrarPath + entrypath);
                if (fh.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    rarfile.extractFile(fh, fileOut);
                    fileOut.close();
                    sum += file.length();
                }
                if (callback != null) {
                    long finalSum = sum;
                    HandlerUtil.runOnUiThread(() -> callback.onProgress(file.getName(), finalSum));
                }
            }
            rarfile.close();
            if (callback != null) {
                HandlerUtil.runOnUiThread(callback::onSucceed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                HandlerUtil.runOnUiThread(() -> callback.onError(e.toString()));
            }
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                    fileOut = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (rarfile != null) {
                try {
                    rarfile.close();
                    rarfile = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void unZipZip(File file, IUnZipCallback callback) {
        Service.getSingleService().execute(() -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                unZipFolder(file.getPath(), Params.getGameDirectory(), callback);
            } else {
                ZipManager.unzip(file.getPath(), Params.getGameDirectory(), new IZipCallback() {
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
        long sum = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            HandlerUtil.runOnUiThread(callback::onStart);
            try {
                inZip = new ZipInputStream(new FileInputStream(zipFileString), Charset.forName("GBK"));
                ZipEntry zipEntry;
                String szName = "";
                while ((zipEntry = inZip.getNextEntry()) != null) {
                    szName = zipEntry.getName();
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
                        }
                        out.close();
                        long finalSum = sum;
                        HandlerUtil.runOnUiThread(() -> callback.onProgress(file.getName(), finalSum));
                    }
                }
                inZip.close();
                HandlerUtil.runOnUiThread(callback::onSucceed);
            } catch (Exception e) {
                Log.e("ZipUtil", "zip解压错误：" + e.toString());
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

    private static void unZipSevenZ(File file, IUnZipCallback callback) {
        Service.getSingleService().execute(() -> Z7Extractor.extractFile(file.getPath(), Params.getGameDirectory(), new IExtractCallback() {
            @Override
            public void onStart() {
                HandlerUtil.runOnUiThread(callback::onStart);
            }

            @Override
            public void onGetFileNum(int fileNum) {

            }

            @Override
            public void onProgress(String name, long size) {
                HandlerUtil.runOnUiThread(() -> callback.onProgress(name, size));
            }

            @Override
            public void onError(int errorCode, String message) {
                HandlerUtil.runOnUiThread(() -> callback.onError(errorCode + " " + message));
            }

            @Override
            public void onSucceed() {
                HandlerUtil.runOnUiThread(callback::onSucceed);
            }
        }));
    }
}
