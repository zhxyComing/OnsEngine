package com.dixon.onsengine.core.monitor;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AppStateRegister {

    private static final Map<String, WeakReference<AppStateTracker.AppStateChangeListener>> trackers = new HashMap<>();

    public static void register(Object clazz, AppStateTracker.AppStateChangeListener listener) {
        if (clazz == null || listener == null) {
            return;
        }
        if (trackers.containsKey(clazz.getClass().getName())
                && Objects.requireNonNull(trackers.get(clazz.getClass().getName())).get() != null) {
            //当前已注册 且注册目标没有被回收
            Log.e(AppStateTracker.class.getSimpleName(), "覆盖旧注册");
        }
        trackers.put(clazz.getClass().getName(), new WeakReference<>(listener));
    }

    public static void unRegister(Object clazz) {
        if (clazz == null) {
            return;
        }
        trackers.remove(clazz.getClass().getName());
    }

    public static List<WeakReference<AppStateTracker.AppStateChangeListener>> getRegisterTrackers() {
        return new ArrayList<>(trackers.values());
    }
}
