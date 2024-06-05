package com.meeleet.cloud.common.auth.security.extension.mobile;

import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailServiceFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

/**
 * 短信验证码认证相关处理配置
 *
 * @author jaychang
 */
@Component
@AllArgsConstructor
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final ExtUserDetailServiceFactory userDetailsServiceFactory;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void configure(HttpSecurity http) {
        //sms code provider
        SmsCodeAuthenticationProvider provider = new SmsCodeAuthenticationProvider();
        provider.setExtUserDetailServiceFactory(userDetailsServiceFactory);
        provider.setRedisTemplate(redisTemplate);
        http.authenticationProvider(provider);
    }
}
