package com.dixon.onsengine.zip;

public class UnZipFactory {

    private UnZipFactory() {
    }

    public static IUnZipExecutor createUnZipExecutor(int type) {
        try {
            switch (type) {
                case IZipType.SevenZ:
                    return SevenZUnZip.class.newInstance();
                case IZipType.RAR:
                    return RarUnZip.class.newInstance();
                case IZipType.ZIP:
                    return ZipUnZip.class.newInstance();
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
