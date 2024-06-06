package com.meeleet.cloud.common.auth.security.userdetails;

import org.springframework.plugin.core.Plugin;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

/**
 * 扩展UserDetailsService接口功能
 * 使得可以支持按手机号、按openid来获取UserDetails
 * @author jaychang
 */
public interface ExtUserDetailsService extends UserDetailsService, Plugin<String> {

    /**
     * 根据手机号加载用户信息
     *
     * @param mobile
     * @return
     * @throws UsernameNotFoundException
     */
    UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException;

    /**
     * 根据手机号加载用户信息(若无此认证方式，则实现类无需实现该方法)
     *
     * @param openid
     * @return
     * @throws UsernameNotFoundException
     */
    default UserDetails loadUserByOpenid(String openid) throws UsernameNotFoundException {
        return null;
    }

    /**
     * 新增用户(当微信认证时，不存在对应openid的用户，需要自动创建用户。针对这种场景下，需要实现该方法)
     *
     */
    default void addUser(Map<String,Object> userInfo) {

    }
}
