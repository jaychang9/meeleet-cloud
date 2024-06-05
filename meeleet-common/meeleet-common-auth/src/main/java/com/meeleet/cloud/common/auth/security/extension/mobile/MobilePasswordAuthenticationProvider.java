package com.meeleet.cloud.common.auth.security.extension.mobile;

import cn.hutool.core.lang.Assert;
import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailServiceFactory;
import com.meeleet.cloud.common.auth.security.userdetails.PreAuthenticationChecks;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 手机号密码认证提供者
 *
 * @author jaychang
 */
public class MobilePasswordAuthenticationProvider implements AuthenticationProvider {
    @Setter
    private ExtUserDetailServiceFactory extUserDetailsServiceFactory;
    @Setter
    private PasswordEncoder passwordEncoder;

    private PreAuthenticationChecks preAuthenticationChecks = new PreAuthenticationChecks();

    @Override
    @SneakyThrows
    public Authentication authenticate(Authentication authentication) {
        MobilePasswordAuthenticationToken authenticationToken = (MobilePasswordAuthenticationToken) authentication;
        String mobile = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();
        UserDetails userDetails = extUserDetailsServiceFactory.getService(authenticationToken).loadUserByMobile(mobile);
        preAuthenticationChecks.check(userDetails);
        Assert.isTrue(passwordEncoder.matches(password, userDetails.getPassword()), () -> {
            throw new BadCredentialsException("手机号或密码错误");
        });
        MobilePasswordAuthenticationToken authenticationResult = new MobilePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    /**
     * providerManager会遍历所有 SecurityConfig中注册的provider集合
     * 根据此方法返回true或false来决定由哪个provider 去校验请求过来的authentication
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return MobilePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
