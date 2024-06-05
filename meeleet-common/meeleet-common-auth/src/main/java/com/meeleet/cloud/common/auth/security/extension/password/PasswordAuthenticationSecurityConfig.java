package com.meeleet.cloud.common.auth.security.extension.password;

import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailServiceFactory;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

/**
 * 密码认证相关处理配置
 *
 * @author jaychang
 */
@Component
@AllArgsConstructor
public class PasswordAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final ExtUserDetailServiceFactory userDetailsServiceFactory;

    @Override
    public void configure(HttpSecurity http) {
        // password provider
        PasswordAuthenticationProvider provider = new PasswordAuthenticationProvider();
        provider.setExtUserDetailServiceFactory(userDetailsServiceFactory);
        // 是否隐藏用户不存在异常 默认:true-隐藏，false-抛出异常
        provider.setHideUserNotFoundExceptions(false);
        http.authenticationProvider(provider);
    }
}
