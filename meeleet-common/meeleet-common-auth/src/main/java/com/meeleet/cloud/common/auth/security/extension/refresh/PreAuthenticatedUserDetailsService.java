package com.meeleet.cloud.common.auth.security.extension.refresh;

import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailsService;
import com.meeleet.cloud.common.base.IBaseEnum;
import com.meeleet.cloud.common.security.enums.AuthenticationIdentityEnum;
import com.meeleet.cloud.common.security.util.RequestUtils;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 刷新token再次认证 UserDetailsService
 *
 * @author jaychang
 */
@NoArgsConstructor
public class PreAuthenticatedUserDetailsService<T extends Authentication> implements AuthenticationUserDetailsService<T>, InitializingBean {

    /**
     * 客户端ID和用户服务 UserDetailService 的映射
     */
    private Map<String, ExtUserDetailsService> userDetailsServiceMap;

    public PreAuthenticatedUserDetailsService(Map<String, ExtUserDetailsService> userDetailsServiceMap) {
        Assert.notNull(userDetailsServiceMap, "UserDetailsServiceMap cannot be null.");
        this.userDetailsServiceMap = userDetailsServiceMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsServiceMap, "userDetailsServiceMap must be set");
    }

    /**
     * 重写PreAuthenticatedAuthenticationProvider 的 preAuthenticatedUserDetailsService 属性，可根据客户端和认证方式选择用户服务 UserDetailService 获取用户信息 UserDetail
     *
     * @param authentication
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
        String clientId = RequestUtils.getOAuth2ClientId();
        // 获取认证身份标识，默认是用户名:username
        AuthenticationIdentityEnum authenticationIdentityEnum = IBaseEnum.getEnumByValue(RequestUtils.getAuthenticationIdentity(), AuthenticationIdentityEnum.class);
        ExtUserDetailsService userDetailsService = userDetailsServiceMap.get(clientId);

        if (AuthenticationIdentityEnum.MOBILE.equals(authenticationIdentityEnum)) {
            return userDetailsService.loadUserByMobile(authentication.getName());
        }
        if (AuthenticationIdentityEnum.OPENID.equals(authenticationIdentityEnum)) {
            return userDetailsService.loadUserByOpenid(authentication.getName());
        }
        return userDetailsService.loadUserByUsername(authentication.getName());
    }
}
