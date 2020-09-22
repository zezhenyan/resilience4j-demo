package com.example.demo.resilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@Slf4j
public class TimeLimiterTest {

    @Test
    public void test(){
        TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.of(TimeLimiterConfig.custom().cancelRunningFuture(true).timeoutDuration(Duration.ofMillis(1000)).build());
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter("test");

        Supplier<Future<Integer>> supplier = () -> create();
        Callable<Integer> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, supplier);
        log.info("start");
        log.info(Try.ofCallable(callable).getOrElse(100).toString());
    }

    @Test
    public void test1(){
        TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.of(TimeLimiterConfig.custom().cancelRunningFuture(true).timeoutDuration(Duration.ofMillis(1000 *100L)).build());
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter("test");

        Supplier<Future<Integer>> supplier = () -> create();
        Callable<Integer> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, supplier);
        log.info("start");
        log.info(Try.ofCallable(callable).getOrElse(100).toString());
    }

    private static Future<Integer> create() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000* 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }
}
