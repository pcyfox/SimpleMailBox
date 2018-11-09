package com.simple.base.utils;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskTools {
    private static final String TAG = "TaskTools";
    private TaskTracker taskTracker;
    private ExecutorService executorService;
    private TaskSubmitter taskSubmitter;

    public TaskTools() {
        taskTracker = new TaskTracker();
        taskSubmitter=new TaskSubmitter();
        // 开启线程池
        executorService = Executors.newSingleThreadExecutor();

    }

    public TaskTracker getTaskTracker() {
        return taskTracker;
    }

    public void setTaskTracker(TaskTracker taskTracker) {
        this.taskTracker = taskTracker;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }


    public TaskSubmitter getTaskSubmitter() {
        return taskSubmitter;
    }

    public void setTaskSubmitter(TaskSubmitter taskSubmitter) {
        this.taskSubmitter = taskSubmitter;
    }

    public class TaskSubmitter {
        @SuppressWarnings("rawtypes")
        public Future submit(Runnable task) {
            Future result = null;
            if (!getExecutorService().isTerminated()
                    && !getExecutorService().isShutdown() && task != null) {
                result = getExecutorService().submit(task);
            }
            return result;
        }

    }

    /**
     * (支持并发的任务计数) Class for monitoring the running task count.
     */
    public class TaskTracker {
        private final AtomicInteger count;

        public TaskTracker() {
            count = new AtomicInteger(0);
        }

        public void increase() {
            count.incrementAndGet();
            Log.d(TAG, "Incremented task count to " + count);
        }

        public void decrease() {
            count.decrementAndGet();
            Log.d(TAG, "Decremented task count to " + count);
        }

    }
}
