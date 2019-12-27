package com.dixon.onsengine.zip;

import com.dixon.onsengine.core.Service;
import com.dixon.onsengine.core.util.HandlerUtil;
import com.hzy.lib7z.IExtractCallback;
import com.hzy.lib7z.Z7Extractor;

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
                        HandlerUtil.runOnUiThread(() -> unZipCallback.onError(errorCode + " " + message));
                    }

                    @Override
                    public void onSucceed() {
                        HandlerUtil.runOnUiThread(unZipCallback::onSucceed);
                    }
                }));
    }
}
