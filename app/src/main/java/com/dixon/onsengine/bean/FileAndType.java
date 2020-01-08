package com.dixon.onsengine.bean;

import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.GameUtil;
import com.dixon.onsengine.zip.IZipType;

import java.io.File;

public class FileAndType implements Comparable<FileAndType> {

    public static final int TYPE_DIR = 0x001;
    public static final int TYPE_ZIP = 0x002;
    public static final int TYPE_UNKNOWN = 0x003;

    private File file;
    private int type;
    private int zipType;
    private int gameType;

    public FileAndType(File file) {
        this.file = file;
        // 识别方式有问题 fix
        if (file.isDirectory()) {
            type = TYPE_DIR;
            gameType = GameUtil.getGameType(file);
        } else {
            switch (FileUtil.getSuffix(file.getName())) {
                case "zip":
                    type = TYPE_ZIP;
                    zipType = IZipType.ZIP;
                    break;
                case "rar":
                    type = TYPE_ZIP;
                    zipType = IZipType.RAR;
                    break;
                case "7z":
                case "7zip":
                    type = TYPE_ZIP;
                    zipType = IZipType.SevenZ;
                    break;
                default:
                    type = TYPE_UNKNOWN;
                    break;
            }
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getZipType() {
        return zipType;
    }

    public void setZipType(int zipType) {
        this.zipType = zipType;
    }

    public boolean isDir() {
        return this.type == TYPE_DIR;
    }

    public boolean isZip() {
        return this.type == TYPE_ZIP;
    }

    public boolean isUnknow() {
        return this.type == TYPE_UNKNOWN;
    }

    public int getGameType() {
        return gameType;
    }

    @Override
    public int compareTo(FileAndType o) {
        if (this.type == o.type && this.type == TYPE_DIR) {
            return this.gameType - o.getGameType();
        } else if (this.type == o.type && this.type == TYPE_ZIP) {
            return this.zipType - o.getZipType();
        }
        return this.type - o.getType();
    }
}
