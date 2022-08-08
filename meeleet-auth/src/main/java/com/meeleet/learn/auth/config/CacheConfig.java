package com.meeleet.learn.auth.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {

    private ThreadPoolExecutor caffeineCacheExecutor = new ThreadPoolExecutor(10, 15, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadFactoryBuilder()
            .setNameFormat("caffeineCacheExecutor-%d")
            .setUncaughtExceptionHandler((thread, e) -> {
                log.error("caffeineCacheExecutor {},{} 发生异常", thread, e);
            }).build());

    @Primary
    @Bean("caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(600, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(200)
                // 使用自定义线程池
                .executor(caffeineCacheExecutor)
                .removalListener(((key, value, cause) -> log.info("key:{} removed, removalCause:{}.", key, cause.name())))
                // 缓存的最大条数
                .maximumSize(2000);

        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Arrays.asList("auth"));
        caffeineCacheManager.setCaffeine(caffeine);
        // 不缓存空值
        caffeineCacheManager.setAllowNullValues(false);
        return caffeineCacheManager;
    }

}
