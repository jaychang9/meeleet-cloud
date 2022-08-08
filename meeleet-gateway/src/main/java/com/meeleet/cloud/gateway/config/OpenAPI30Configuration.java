package com.meeleet.cloud.gateway.config;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
@Slf4j
@Profile("!prod")
public class OpenAPI30Configuration {

    private static final String IGNORE_DEFINITION_ID_PREFIX = "ReactiveCompositeDiscoveryClient_";

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters, RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        if (definitions != null && definitions.size() > 0) {
            for (RouteDefinition definition : definitions) {
                String definitionId = definition.getId();
                if (!Objects.equals(definitionId, "openapi") && !StrUtil.startWithIgnoreCase(definitionId, IGNORE_DEFINITION_ID_PREFIX)) {
                    swaggerUiConfigParameters.addGroup(definition.getId());
                }
            }
        }
        return groups;
    }
}