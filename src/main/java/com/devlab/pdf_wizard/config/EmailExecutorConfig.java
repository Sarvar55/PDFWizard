package com.devlab.pdf_wizard.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailExecutorConfig {

    @Bean(name = "mailExecutor", destroyMethod = "shutdown")
    public ExecutorService mailExecutor() {
        return new ThreadPoolExecutor(
                2,
                4,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                mailThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    private ThreadFactory mailThreadFactory() {
        AtomicInteger counter = new AtomicInteger();

        return task -> {
            Thread thread = new Thread(task);
            thread.setName("pdf-mail-" + counter.incrementAndGet());
            thread.setDaemon(false);
            return thread;
        };
    }
}
