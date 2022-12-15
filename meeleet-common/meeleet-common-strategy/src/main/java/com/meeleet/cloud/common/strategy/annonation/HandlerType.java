package com.meeleet.cloud.common.strategy.annonation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 策略模式注解
 *
 * @author jaychang
 * @date 2022-12-15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface HandlerType {

    String type();

    String source();
}
