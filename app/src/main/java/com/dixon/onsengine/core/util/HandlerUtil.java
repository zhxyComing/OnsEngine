package com.dixon.onsengine.core.util;

import android.os.Handler;
import android.os.Looper;

public class HandlerUtil {

    private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    private HandlerUtil() {

    }

    public static void runOnUiThread(Runnable r) {
        if (Thread.currentThread().getId() == UI_HANDLER.getLooper().getThread().getId()) {
            r.run();
        } else {
            UI_HANDLER.post(r);
        }
    }
}
