package com.meeleet.cloud.common.auth.security.userdetails;

import cn.hutool.core.util.StrUtil;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 用户service工厂
 * 插件模式
 *
 * @author jaychang
 * @version 1.0
 */
@Slf4j
@Component
@EnablePluginRegistries(ExtUserDetailsService.class)
public class UserDetailServiceFactory {
    private static final String ERROR_MSG = "找不到账号类型为 %s 的实现类";

    private final PluginRegistry<ExtUserDetailsService, String> extUserDetailsServiceRegistry;

    public UserDetailServiceFactory(@Qualifier("extUserDetailsServiceRegistry") PluginRegistry<ExtUserDetailsService, String> extUserDetailsServiceRegistry) {
        this.extUserDetailsServiceRegistry = extUserDetailsServiceRegistry;
    }

    public ExtUserDetailsService getService(Authentication authentication) {
        // TODO 哪里获取账户类型
        // String accountType = AuthUtils.getAccountType(authentication);
        String accountType = "";
        if (StrUtil.isEmpty(accountType)) {
            accountType = SecurityConstants.MALL_MEMBER_USER_TYPE;
        }
        return this.getService(accountType);
    }

    public ExtUserDetailsService getService(final String accountType) {
        return Optional.ofNullable(extUserDetailsServiceRegistry.getPluginFor(accountType))
                .orElseThrow(() -> new InternalAuthenticationServiceException(StrUtil.format(ERROR_MSG, accountType))).get();
    }

}
