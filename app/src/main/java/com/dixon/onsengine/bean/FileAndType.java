package com.dixon.onsengine.bean;

import com.dixon.onsengine.core.util.FileUtil;

import java.io.File;

public class FileAndType {

    public static final int TYPE_GAME = 0x001;
    public static final int TYPE_ZIP = 0x002;
    public static final int TYPE_UNKNOW = 0x003;

    private File file;
    private int type;
    private int zipType;

    public FileAndType(File file) {
        this.file = file;
        if (file.isDirectory()) {
            type = TYPE_GAME;
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
                    type = TYPE_UNKNOW;
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

    public boolean isGame() {
        return this.type == TYPE_GAME;
    }

    public boolean isZip() {
        return this.type == TYPE_ZIP;
    }

    public boolean isUnknow() {
        return this.type == TYPE_UNKNOW;
    }
}
