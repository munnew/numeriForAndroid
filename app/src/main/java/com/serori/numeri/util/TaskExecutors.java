package com.serori.numeri.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 */
public final class TaskExecutors {
    private final Map<String, ExecutorService> executorServiceMap = new LinkedHashMap<>();

    private TaskExecutors() {

    }

    public TaskExecutors getInstance() {
        return TaskExecutorsHolder.instance;
    }

    public ExecutorService createThreadPool(String name, int thread) {
        ExecutorService executorService = Executors.newFixedThreadPool(thread);
        executorServiceMap.put(name, executorService);
        return executorService;
    }

    public ExecutorService getThreadPool(String name) {
        return executorServiceMap.get(name);
    }

    private static class TaskExecutorsHolder {
        private final static TaskExecutors instance = new TaskExecutors();
    }
}
