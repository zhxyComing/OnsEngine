package com.dixon.onsengine.core.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtil {

    public static List<File> getFileList(String path) {
        ArrayList<File> arrayList = new ArrayList<>();
        File file = new File(path);
        if (!file.exists() && !file.mkdirs()) {
            Log.e("FileUtil", "Create Fail");
            return arrayList;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                Collections.addAll(arrayList, files);
            }
        }
        Collections.sort(arrayList, new FileNameComparator());
        return arrayList;
    }

    public static List<File> getDirList(String path) {
        ArrayList<File> arrayList = new ArrayList<>();
        File file = new File(path);
        if (!file.exists() && !file.mkdirs()) {
            Log.e("FileUtil", "Create Fail");
            return arrayList;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        arrayList.add(f);
                    }
                }
            }
        }
        Collections.sort(arrayList, new FileNameComparator());
        return arrayList;
    }

    public static int getDirCount(File file) {
        int count = 0;
        if (!file.exists()) {
            return 0;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        }
        return "";
    }

    public static String getSuffix(String fileName) {
        if (fileName.contains(".")) {
            String[] split = fileName.split("\\.");
            String suffix = split[split.length - 1];
            return suffix;
        }
        return "";
    }

    // 抛去后缀
    public static String getFileName(File file) {
        String realName = file.getName();
        if (realName.contains(".")) {
            String[] split = realName.split("\\.");
            realName = split[0];
        }
        Log.e("FileUtil", "RealFileName is " + realName);
        return realName;
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            if (fileList == null) {
                return 0;
            }
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) size = size + getFolderSize(fileList[i]);
                else size = size + fileList[i].length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    private static final class FileNameComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }

    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            file.delete();
        }
    }

    // 删除mac下压缩生成的垃圾文件
    public static void deleteMacOsJunkFile(String saveDir) {
        File macos = new File(saveDir + File.separator + "__MACOSX");
        if (macos.exists()) {
            FileUtil.deleteFile(macos);
        }
    }

    //删除文件夹
    private static void deleteDirectory(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        folder.delete();
    }

    public static String getFromAssets(String fileName, Context context) {
        // load text
        try {
            // get input stream for text
            InputStream is = context.getAssets().open(fileName);
            // check size
            int size = is.available();
            // create buffer for IO
            byte[] buffer = new byte[size];
            // get data to buffer
            is.read(buffer);
            // close stream
            is.close();
            // set result to TextView
            return new String(buffer);
        } catch (IOException ex) {
            return "";
        }
    }

    /**
     * 通过反射调用获取内置存储和外置sd卡根路径(通用)
     *
     * @param mContext 上下文
     * @param tag      是否可移除，false返回内部存储，true返回外置sd卡
     * @return
     */
    public static String getStoragePath(Context mContext, boolean tag) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (tag == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
