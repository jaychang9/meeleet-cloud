package com.meeleet.cloud.common.auth.security.extension.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailServiceFactory;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

/**
 * 微信openId认证相关处理配置
 *
 * @author jaychang
 */
@Component
@AllArgsConstructor
public class WechatAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final ExtUserDetailServiceFactory userDetailsServiceFactory;
    private final WxMaService wxMaService;

    @Override
    public void configure(HttpSecurity http) {
        //sms code provider
        WechatAuthenticationProvider provider = new WechatAuthenticationProvider();
        provider.setExtUserDetailServiceFactory(userDetailsServiceFactory);
        provider.setWxMaService(wxMaService);
        http.authenticationProvider(provider);
    }
}
