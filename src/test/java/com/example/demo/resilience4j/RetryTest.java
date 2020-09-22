package com.example.demo.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

@Slf4j
public class RetryTest {

    @Test
    public void test(){
        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(3)
                .retryOnException(throwable -> {
                    log.error("retry issue:", throwable);
                    // continue retry once return true.
                    return true;
                })
                .build();
        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);
        Retry retry = retryRegistry.retry("test");
        Callable<Integer> callable = Retry.decorateCallable(retry, create());
        System.out.println(Try.ofCallable(callable).getOrElse(100));
    }
    private static Callable<Integer> create() {
        return () -> {
            int i = 1/0;
            return 0;
        };
    }

}
