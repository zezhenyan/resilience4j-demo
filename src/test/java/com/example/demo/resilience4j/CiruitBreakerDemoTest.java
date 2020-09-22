package com.example.demo.resilience4j;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class CiruitBreakerDemoTest {

    @Test
    public void test() throws InterruptedException {
        // init Circuit Breaker
        CircuitBreaker circuitBreaker = initCircuitBreaker(circuitBreakerRegistry(), "test");
        for(int i = 0;i< 30; i ++) {
            try {
                System.out.println(circuitBreaker.executeCallable(() -> {
                    System.out.println(Thread.currentThread().getName());
                    int ii = 1 / 0;
                    return ii;

                }));
            } catch (CallNotPermittedException e) {
                System.out.println(i + "==" + e);
                Thread.sleep(1000L);
                System.out.println(i + "==" + circuitBreaker.getState());
            } catch (Exception e) {
                System.out.println(i + "--" + e);
                System.out.println(i + "--" + circuitBreaker.getState());
            }
        }
        for(int i = 100;i< 200; i ++) {
            try {
                int finalI = i;
                System.out.println(circuitBreaker.executeCallable(() -> {
                    int ii = finalI;
                    return ii;

                })+"!!!"+circuitBreaker.getState());
            } catch (CallNotPermittedException e) {
                System.out.println(i + "==" + e);
                Thread.sleep(1000L);
                System.out.println(i + "==" + circuitBreaker.getState());
            } catch (Exception e) {
                System.out.println(i + "--" + e);
                System.out.println(i + "--" + circuitBreaker.getState());
            }
        }
    }


    public static CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(10)
                .waitDurationInOpenState(Duration.ofSeconds(1))
                .build();
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }
    private static CircuitBreaker initCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry, String serviceName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);
        circuitBreaker.getEventPublisher()
                .onError(event -> log.warn("Circuit breaker failure: {}", event.toString()))
                .onCallNotPermitted(event -> log.warn("Circuit breaker call not permitted: {}", event.toString()))
                .onIgnoredError(event -> log.warn("Circuit breaker error ignored: {}", event.toString()))
                .onStateTransition(event -> log.warn("Circuit breaker state change: {}", event.toString()));
        return circuitBreaker;
    }
}
