package com.meeleet.learn.gateway;

import com.meeleet.learn.common.redis.RedisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@Import({RedisConfig.class})
@EnableDiscoveryClient
@SpringBootApplication
public class LearnGatewayApplication {


	public static void main(String[] args) {
		SpringApplication.run(LearnGatewayApplication.class, args);
	}

}