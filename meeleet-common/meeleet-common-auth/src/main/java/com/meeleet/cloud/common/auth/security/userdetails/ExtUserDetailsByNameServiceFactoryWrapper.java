package com.meeleet.cloud.common.auth.security.userdetails;

import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;

/**
 * 重写 UserDetailsByNameServiceWrapper 支持多帐户类型
 *
 * @author zlt
 * @version 1.0
 * @date 2018/7/24
 * @see UserDetailsByNameServiceWrapper
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class ExtUserDetailsByNameServiceFactoryWrapper<T extends Authentication> implements AuthenticationUserDetailsService<T>, InitializingBean {
    @Setter
    private ExtUserDetailServiceFactory extUserDetailServiceFactory;

    public ExtUserDetailsByNameServiceFactoryWrapper() {
    }

    public ExtUserDetailsByNameServiceFactoryWrapper(final ExtUserDetailServiceFactory extUserDetailServiceFactory) {
        Assert.notNull(extUserDetailServiceFactory, "userDetailServiceFactory cannot be null.");
        this.extUserDetailServiceFactory = extUserDetailServiceFactory;
    }

    /**
     * Check whether all required properties have been set.
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.extUserDetailServiceFactory, "ExtUserDetailServiceFactory must be set");
    }

    /**
     * Get the UserDetails object from the wrapped UserDetailsService implementation
     */
    @Override
    public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
        ExtUserDetailsService userDetailsService;
        if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            userDetailsService = this.extUserDetailServiceFactory.getService((Authentication) authentication.getPrincipal());
        } else {
            userDetailsService = this.extUserDetailServiceFactory.getService(authentication);
        }
        return userDetailsService.loadUserByUsername(authentication.getName());
    }
}
