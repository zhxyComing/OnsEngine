package com.dixon.onsengine.core.util;

import com.dixon.onsengine.zip.IUnZipCallback;
import com.dixon.onsengine.zip.IUnZipExecutor;
import com.dixon.onsengine.zip.IZipType;
import com.dixon.onsengine.core.Params;
import com.dixon.onsengine.zip.UnZipFactory;

import java.io.File;


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

    // rar
    private static void unZipRar(File srcFile, IUnZipCallback callback) {
        IUnZipExecutor unZipExecutor = UnZipFactory.createUnZipExecutor(IZipType.RAR);
        unZip(unZipExecutor, srcFile, callback);
    }

    // zip
    private static void unZipZip(File file, IUnZipCallback callback) {
        IUnZipExecutor unZipExecutor = UnZipFactory.createUnZipExecutor(IZipType.ZIP);
        unZip(unZipExecutor, file, callback);
    }

    // 7z
    private static void unZipSevenZ(File file, IUnZipCallback callback) {
        IUnZipExecutor unZipExecutor = UnZipFactory.createUnZipExecutor(IZipType.SevenZ);
        unZip(unZipExecutor, file, callback);
    }

    private static void unZip(IUnZipExecutor executor, File file, IUnZipCallback callback) {
        if (executor != null) {
            executor.unZip(file, Params.getGameDirectory(), callback);
        }
    }
}
