package com.dixon.onsengine.zip;

import java.io.File;

public interface IUnZipExecutor {

    void unZip(File zipFile, String saveDirectory, IUnZipCallback unZipCallback);

    void unZipWithSecret(File zipFile, String saveDirectory, IUnZipCallback unZipCallback, String password);
}
