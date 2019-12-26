package com.dixon.onsengine.bean;

public interface IUnZipCallback {

    void onStart();

    void onProgress(String name, long size);

    void onError(String message);

    void onSucceed();

    void onProcess(int process);
}
