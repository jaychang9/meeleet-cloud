package com.meeleet.learn.common.redis;

import com.meeleet.learn.common.redis.constant.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


@Component
@Slf4j
public class BusinessNoGenerator {

    private final RedisTemplate redisTemplate;

    public BusinessNoGenerator(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * @param businessType 业务类型枚举
     * @param digit        业务序号位数
     * @return
     */
    public String generate(String businessType, Integer digit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        String key = RedisConstants.BUSINESS_NO_PREFIX + businessType + ":" + date;
        Long increment = redisTemplate.opsForValue().increment(key);
        return date + businessType + String.format("%0" + digit + "d", increment);
    }

    public String generate(String businessType) {
        Integer defaultDigit = 6;
        return generate(businessType, defaultDigit);
    }

}
