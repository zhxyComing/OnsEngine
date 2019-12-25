package com.dixon.onsengine.core;

import com.dixon.onsengine.core.util.FileUtil;

public class Params {

    private static final String GAME_DIRECTORY = "/OERunner";

    public static String getGameDirectory() {

        return FileUtil.getSDPath() + Params.GAME_DIRECTORY;
    }
}
