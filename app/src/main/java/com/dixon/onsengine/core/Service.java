package com.dixon.onsengine.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service {

    private static final ExecutorService singleService = Executors.newSingleThreadExecutor();

    public static ExecutorService getSingleService() {
        return singleService;
    }
}
