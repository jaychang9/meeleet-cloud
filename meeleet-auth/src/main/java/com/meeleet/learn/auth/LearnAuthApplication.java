package com.meeleet.cloud.auth;

import com.meeleet.cloud.common.redis.RedisConfig;
import com.meeleet.cloud.common.web.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

// TODO 这里可以用借鉴使用自动加载
@Import({GlobalExceptionHandler.class, RedisConfig.class})
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LearnAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(LearnAuthApplication.class, args);
    }

}