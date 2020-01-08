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
     * 后缀有 .nsa 的文件夹才是 ons 游戏。
     *
     * @param dir
     * @return
     */
    public static boolean isOnsGame(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    String suffix = FileUtil.getSuffix(name);
                    if ("nsa".equals(suffix)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isKrGame(File dir) {
        //todo
        return false;
    }
}
