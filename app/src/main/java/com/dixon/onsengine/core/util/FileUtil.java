package com.dixon.onsengine.core.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
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

    private static final class FileNameComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }
}
