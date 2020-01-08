package com.dixon.onsengine.zip;

import com.dixon.onsengine.core.Service;
import com.dixon.onsengine.core.util.FileUtil;
import com.dixon.onsengine.core.util.HandlerUtil;
import com.hzy.lib7z.IExtractCallback;
import com.hzy.lib7z.Z7Extractor;
import com.hzy.libp7zip.ExitCode;
import com.hzy.libp7zip.P7ZipApi;

import java.io.File;

public class SevenZUnZip implements IUnZipExecutor {

    @Override
    public void unZip(File zipFile, String saveDirectory, IUnZipCallback unZipCallback) {
        Service.getSingleService().execute(() ->
                Z7Extractor.extractFile(zipFile.getPath(), saveDirectory, new IExtractCallback() {
                    @Override
                    public void onStart() {
                        HandlerUtil.runOnUiThread(unZipCallback::onStart);
                    }

                    @Override
                    public void onGetFileNum(int fileNum) {

                    }

                    @Override
                    public void onProgress(String name, long size) {
                        HandlerUtil.runOnUiThread(() -> unZipCallback.onProgress(name, size));
                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        deleteTempDir(zipFile, saveDirectory);
                        HandlerUtil.runOnUiThread(() -> unZipCallback.onError(errorCode + " " + message));
                    }

                    @Override
                    public void onSucceed() {
                        FileUtil.deleteMacOsJunkFile(saveDirectory);
                        HandlerUtil.runOnUiThread(unZipCallback::onSucceed);
                    }
                }));
    }

    @Override
    public void unZipWithSecret(File zipFile, String saveDirectory, IUnZipCallback unZipCallback, String password) {
        unZipCallback.onStart();
        Service.getSingleService().execute(() -> {
            unZipCallback.onProgress("解压中，暂无进度显示，请耐心等待", 0);
            int result = P7ZipApi.executeCommand(getExtractSecretCmd(zipFile.getPath(), saveDirectory, password));
            HandlerUtil.runOnUiThread(() -> showResult(result, saveDirectory, unZipCallback));
        });
    }

    private void showResult(int result, String saveDir, IUnZipCallback unZipCallback) {
        switch (result) {
            case ExitCode.EXIT_OK:
                FileUtil.deleteMacOsJunkFile(saveDir);
                unZipCallback.onProgress("解压完毕", 0);
                unZipCallback.onSucceed();
                break;
            case ExitCode.EXIT_WARNING:
            case ExitCode.EXIT_FATAL:
            case ExitCode.EXIT_CMD_ERROR:
            case ExitCode.EXIT_MEMORY_ERROR:
            case ExitCode.EXIT_NOT_SUPPORT:
                FileUtil.deleteMacOsJunkFile(saveDir);
                unZipCallback.onError("解压失败");
        }
    }

    public static String getExtractCmd(String archivePath, String outPath) {
        return String.format("7z x '%s' '-o%s' -aoa", archivePath, outPath);
    }

    public static String getExtractSecretCmd(String archivePath, String outPath, String secret) {
        return String.format("7z x '%s' '-o%s' '-p%s' -aoa", archivePath, outPath, secret);
    }

    private void deleteTempDir(File zipFile, String saveDir) {
        File file = new File(saveDir + File.separator + FileUtil.getFileName(zipFile));
        if (file.exists()) {
            FileUtil.deleteFile(file);
        }
        FileUtil.deleteMacOsJunkFile(saveDir);
    }
}
