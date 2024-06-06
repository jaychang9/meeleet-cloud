package com.meeleet.cloud.common.auth.security.userdetails;

import cn.hutool.core.util.StrUtil;
import com.meeleet.cloud.common.security.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 用户service工厂(插件模式)
 * spring.factories 已指定会扫描该类
 * @author jaychang
 * @version 1.0
 */
@Slf4j
@Component
@EnablePluginRegistries(ExtUserDetailsService.class)
public class ExtUserDetailServiceFactory {
    private static final String ERROR_MSG = "找不到客户端ID为 %s 的实现类";

    private final PluginRegistry<ExtUserDetailsService, String> extUserDetailsServiceRegistry;

    public ExtUserDetailServiceFactory(PluginRegistry<ExtUserDetailsService, String> extUserDetailsServiceRegistry) {
        this.extUserDetailsServiceRegistry = extUserDetailsServiceRegistry;
    }

    public ExtUserDetailsService getService(final Authentication authentication) {
        String clientId = RequestUtils.getOAuth2ClientId();
        if (log.isDebugEnabled()) {
            log.debug("根据clientId:{}获取 ExtUserDetailsService ", clientId);
        }
        return this.getService(clientId);
    }

    public ExtUserDetailsService getService(final String clientId) {
        return Optional.ofNullable(extUserDetailsServiceRegistry.getPluginFor(clientId))
                .orElseThrow(() -> new InternalAuthenticationServiceException(StrUtil.format(ERROR_MSG, clientId))).get();
    }

}
