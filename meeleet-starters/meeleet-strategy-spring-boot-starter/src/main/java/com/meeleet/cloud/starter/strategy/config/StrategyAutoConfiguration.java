package com.meeleet.cloud.starter.strategy.config;

import com.meeleet.cloud.common.strategy.handler.BusinessHandler;
import com.meeleet.cloud.common.strategy.handler.BusinessHandlerChooser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 策略模式自动注入配置
 *
 * @author jaychang
 * @date 2022-12-15
 */
@Configuration
public class StrategyAutoConfiguration {

    @Bean
    public BusinessHandlerChooser businessHandlerChooser(List<BusinessHandler> businessHandlers) {
        BusinessHandlerChooser businessHandlerChooser = new BusinessHandlerChooser();
        businessHandlerChooser.setBusinessHandlerMap(businessHandlers);
        return businessHandlerChooser;
    }

}
