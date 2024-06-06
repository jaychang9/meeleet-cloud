package com.meeleet.cloud.common.auth.security.extension.mobile;

import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailServiceFactory;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

/**
 * 短信验证码认证相关处理配置
 * 目前这种方式没生效，故目前该类没在使用(目前测试情况，ProviderManager authenticate 方法执行的时候会 stackoverflow，还未确认具体原因)
 * @author jaychang
 */
@Component
@AllArgsConstructor
public class MobilePasswordAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final ExtUserDetailServiceFactory userDetailsServiceFactory;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void configure(HttpSecurity http) {
        //sms code provider
        MobilePasswordAuthenticationProvider provider = new MobilePasswordAuthenticationProvider();
        provider.setExtUserDetailsServiceFactory(userDetailsServiceFactory);
        provider.setPasswordEncoder(passwordEncoder);
        http.authenticationProvider(provider);
    }
}
