package com.example.applicationserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThreadController {

    @Autowired
    @Qualifier("factorialExecutor")
    private AsyncTaskExecutor taskExecutor;

    @GetMapping("/active-threads")
    public int getActiveThreads() {
        if (taskExecutor instanceof ThreadPoolTaskExecutor) {
            int activeThreads = ((ThreadPoolTaskExecutor) taskExecutor).getActiveCount();
            int queueSize = ((ThreadPoolTaskExecutor) taskExecutor).getThreadPoolExecutor().getQueue().size();
            System.out.println("Кількість активних потоків: " + activeThreads);
            System.out.println("Розмір черги: " + queueSize);
            return activeThreads;
        }
        return -1;
    }
}