package com.meeleet.cloud.starter.redis.config;

import com.meeleet.cloud.common.redis.BusinessNoGenerator;
import com.meeleet.cloud.starter.redis.annotaion.EnableBusinessNo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@ConditionalOnBean(annotation = EnableBusinessNo.class)
public class BusinessNoGeneratorAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BusinessNoGenerator businessNoGenerator(RedisTemplate redisTemplate) {
        return new BusinessNoGenerator(redisTemplate);
    }
}
