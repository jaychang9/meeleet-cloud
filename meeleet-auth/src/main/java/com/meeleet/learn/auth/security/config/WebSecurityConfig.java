package com.meeleet.cloud.auth.security.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.meeleet.cloud.auth.security.core.userdetails.member.MemberUserDetailsServiceImpl;
import com.meeleet.cloud.auth.security.core.userdetails.sysuser.SysUserDetailsServiceImpl;
import com.meeleet.cloud.auth.security.extension.mobile.SmsCodeAuthenticationProvider;
import com.meeleet.cloud.auth.security.extension.password.DaoxAuthenticationProvider;
import com.meeleet.cloud.auth.security.extension.wechat.WechatAuthenticationProvider;
import com.meeleet.cloud.auth.security.userdetails.ExtUserDetailsService;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@Slf4j
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SysUserDetailsServiceImpl sysUserDetailsService;
    private final MemberUserDetailsServiceImpl memberUserDetailsService;
    private final WxMaService wxMaService;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/oauth/**", "/sms-code/**").permitAll()
                // swagger2用以下代码
                //.antMatchers("/webjars/**", "/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs").permitAll()
                // knife4j用以下代码
                // .antMatchers("/webjars/**", "/doc.html", "/swagger-resources/**", "/v2/api-docs").permitAll()
                // spring-doc swagger3用以下代码
                .antMatchers("/webjars/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * 认证管理对象
     *
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(wechatAuthenticationProvider()).
                authenticationProvider(daoAuthenticationProvider()).
                authenticationProvider(smsCodeAuthenticationProvider());
    }

    /**
     * 手机验证码认证授权提供者
     *
     * @return
     */
    @Bean
    public SmsCodeAuthenticationProvider smsCodeAuthenticationProvider() {
        SmsCodeAuthenticationProvider provider = new SmsCodeAuthenticationProvider();
        provider.setUserDetailsServiceMap(userDetailsServiceMap());
        provider.setRedisTemplate(redisTemplate);
        return provider;
    }

    /**
     * 微信认证授权提供者
     *
     * @return
     */
    @Bean
    public WechatAuthenticationProvider wechatAuthenticationProvider() {
        WechatAuthenticationProvider provider = new WechatAuthenticationProvider();
        provider.setUserDetailsServiceMap(userDetailsServiceMap());
        provider.setWxMaService(wxMaService);
        return provider;
    }


    /**
     * 用户名密码认证授权提供者
     *
     * @return
     */
    @Bean
    public DaoxAuthenticationProvider daoAuthenticationProvider() {
        DaoxAuthenticationProvider provider = new DaoxAuthenticationProvider();
        provider.setUserDetailsServiceMap(userDetailsServiceMap());
        provider.setPasswordEncoder(passwordEncoder());
        // 是否隐藏用户不存在异常 默认:true-隐藏，false-抛出异常
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    /**
     * 用于支持不同用户体系相同认证方式grant_type的情况
     *
     * @return
     */
    @Bean
    public Map<String, ExtUserDetailsService> userDetailsServiceMap() {
        Map<String, ExtUserDetailsService> clientIdUserDetailsServiceMap = new HashMap<>();
        clientIdUserDetailsServiceMap.put(SecurityConstants.ADMIN_CLIENT_ID,sysUserDetailsService);
        clientIdUserDetailsServiceMap.put(SecurityConstants.APP_CLIENT_ID,memberUserDetailsService);
        clientIdUserDetailsServiceMap.put(SecurityConstants.WEAPP_CLIENT_ID,memberUserDetailsService);
        return clientIdUserDetailsServiceMap;
    }


    /**
     * 密码编码器
     * <p>
     * 委托方式，根据密码的前缀选择对应的encoder，例如：{bcypt}前缀->标识BCYPT算法加密；{noop}->标识不使用任何加密即明文的方式
     * 密码判读 DaoAuthenticationProvider#additionalAuthenticationChecks
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}