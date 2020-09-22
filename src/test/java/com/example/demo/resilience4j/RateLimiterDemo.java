package com.example.demo.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Slf4j
public class RateLimiterDemo {

    @Test
    public void test(){
        AtomicInteger atomicInteger = new AtomicInteger(0);
        RateLimiterConfig config = RateLimiterConfig.custom() .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(1).build();
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        RateLimiter rateLimiter = registry.rateLimiter("my");
        Supplier<Integer> supplier =
                RateLimiter.decorateSupplier(rateLimiter,
                        () -> atomicInteger.incrementAndGet());

//        Supplier<Integer> supplier =
//                RateLimiter.decorateSupplier(rateLimiter,
//                        new Supplier<Integer>() {
//                            @Override
//                            public Integer get() {
//                                return atomicInteger.incrementAndGet();
//                            }
//                        });
        for(int i = 0;i< 10; i++) {
            log.info(supplier.get().toString());
        }
    }

}
