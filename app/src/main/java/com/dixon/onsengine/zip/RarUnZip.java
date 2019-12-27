package com.dixon.onsengine.zip;

import com.dixon.onsengine.core.Service;
import com.dixon.onsengine.core.util.HandlerUtil;

import java.io.File;
import java.io.FileOutputStream;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

public class RarUnZip implements IUnZipExecutor {

    @Override
    public void unZip(File srcFile, String unRarPath, IUnZipCallback callback) {

        // 拼接出正确的路径
        if (null == unRarPath || "".equals(unRarPath)) {
            unRarPath = srcFile.getParentFile().getPath();
        }
        // 保证文件夹路径最后是"/"或者"\"
        char lastChar = unRarPath.charAt(unRarPath.length() - 1);
        if (lastChar != '/' && lastChar != '\\') {
            unRarPath += File.separator;
        }
        String realName;
        if (srcFile.getName().contains(".")) {
            String[] split = srcFile.getName().split("\\.");
            realName = split[0];
        } else {
            realName = srcFile.getName();
        }
        unRarPath = unRarPath + realName + File.separator;

        // 回调start
        if (callback != null) {
            callback.onStart();
        }

        String finalUnRarPath = unRarPath;
        // 工作线程开始解压
        Service.getSingleService().execute(() -> {
            FileOutputStream fileOut = null;
            Archive rarFile = null;
            try {
                rarFile = new Archive(srcFile);
                FileHeader fh = null;
                long sum = 0;
                for (int i = 0; i < rarFile.getFileHeaders().size(); i++) {
                    fh = rarFile.getFileHeaders().get(i);
                    String entrypath = "";
                    if (fh.isUnicode()) {//解決中文乱码
                        entrypath = fh.getFileNameW().trim();
                    } else {
                        entrypath = fh.getFileNameString().trim();
                    }
                    entrypath = entrypath.replaceAll("\\\\", "/");
                    File file = new File(finalUnRarPath + entrypath);
                    if (fh.isDirectory()) {
                        file.mkdirs();
                    } else {
                        File parent = file.getParentFile();
                        if (parent != null && !parent.exists()) {
                            parent.mkdirs();
                        }
                        fileOut = new FileOutputStream(file);
                        rarFile.extractFile(fh, fileOut);
                        fileOut.close();
                        sum += file.length();
                    }
                    // rar解压只会在解压完一个完整文件后才回调 所以不用限制频率
                    if (callback != null) {
                        long finalSum = sum;
                        HandlerUtil.runOnUiThread(() -> callback.onProgress(file.getName(), finalSum));
                    }
                }
                rarFile.close();
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
                if (rarFile != null) {
                    try {
                        rarFile.close();
                        rarFile = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
