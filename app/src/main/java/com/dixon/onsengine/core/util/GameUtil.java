package com.dixon.onsengine.core.util;

import com.dixon.onsengine.core.enumbean.GameType;

import java.io.File;

public class GameUtil {

    private GameUtil() {
    }

    public static int getGameType(File dir) {
        if (isOnsGame(dir)) {
            return GameType.ONS;
        } else if (isKrGame(dir)) {
            return GameType.KRKR;
        }
        return GameType.UNKNOW;
    }

    /**
     * 后缀有.nsa的文件夹才是ons游戏。
     *
     * @param dir
     * @return
     */
    public static boolean isOnsGame(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                return false;
            }
            for (File f : files) {
                if (!f.isDirectory()) {
                    // 说明是ons文件 直接返回true
                    if ("nsa".equals(FileUtil.getSuffix(f.getName()))) {
                        return true;
                    }
                }
            }
            // 遍历一层文件没找到 所以开始遍历下一层
            for (File f : files) {
                if (f.isDirectory()) {
                    return isOnsGame(f);
                }
            }
        }
        // 没有找到.nsa的文件，所以不是ons文件。
        return false;
    }

    public static boolean isKrGame(File dir) {
        //todo
        return false;
    }

    public static String getGameRealPath(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return null;
            }
            for (File f : files) {
                if (!f.isDirectory()) {
                    // 找到后返回当前文件夹目录
                    if ("nsa".equals(FileUtil.getSuffix(f.getName()))) {
                        return path;
                    }
                }
            }
            // 遍历一层文件没找到 所以开始遍历下一层
            for (File f : files) {
                if (f.isDirectory()) {
                    return getGameRealPath(f.getPath());
                }
            }
        }
        // 没有找到带nsa的文件，所以不是游戏文件。
        return null;
    }

    public static String getGameIconPath(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return null;
            }
            for (File f : files) {
                if (!f.isDirectory()) {
                    // 找到后返回当前图片路径
                    if ("icon.png".equals((f.getName()))) {
                        return f.getPath();
                    }
                }
            }
            // 遍历一层文件没找到 所以开始遍历下一层
            for (File f : files) {
                if (f.isDirectory()) {
                    return getGameIconPath(f);
                }
            }
        }
        // 没有找到带nsa的文件，所以不是游戏文件。
        return null;
    }
}
