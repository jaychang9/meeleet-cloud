package com.meeleet.cloud.common.strategy.handler;


import com.meeleet.cloud.common.strategy.annonation.HandlerType;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 业务处理策略选择器
 *
 * @author jaychang
 * @date 2022-12-15
 */
public class BusinessHandlerChooser {

    private Map<HandlerType, BusinessHandler> businessHandlerMap;

    public void setBusinessHandlerMap(List<BusinessHandler> businessHandlers) {
        businessHandlerMap = businessHandlers.stream().collect(
                Collectors.toMap(businessHandler -> AnnotationUtils.findAnnotation(businessHandler.getClass(), HandlerType.class),
                        Function.identity(), (v1, v2) -> v1));
    }

    public <R, T> BusinessHandler<R, T> businessHandlerChooser(String type, String source) {
        HandlerType orderHandlerType = new HandlerTypeImpl(type, source);
        return businessHandlerMap.get(orderHandlerType);
    }
}
