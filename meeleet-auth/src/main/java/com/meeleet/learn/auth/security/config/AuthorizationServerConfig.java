package com.meeleet.learn.auth.security.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.meeleet.learn.auth.security.core.clientdetails.ClientDetailsServiceImpl;
import com.meeleet.learn.auth.security.core.userdetails.member.MemberUserDetails;
import com.meeleet.learn.auth.security.core.userdetails.member.MemberUserDetailsServiceImpl;
import com.meeleet.learn.auth.security.core.userdetails.sysuser.SysUserDetails;
import com.meeleet.learn.auth.security.core.userdetails.sysuser.SysUserDetailsServiceImpl;
import com.meeleet.learn.auth.security.extension.captcha.CaptchaTokenGranter;
import com.meeleet.learn.auth.security.extension.mobile.SmsCodeTokenGranter;
import com.meeleet.learn.auth.security.extension.refresh.PreAuthenticatedUserDetailsService;
import com.meeleet.learn.auth.security.extension.wechat.WechatTokenGranter;
import com.meeleet.learn.auth.security.filter.CustomClientCredentialsTokenEndpointFilter;
import com.meeleet.learn.auth.security.userdetails.ExtUserDetailsService;
import com.meeleet.learn.common.result.Result;
import com.meeleet.learn.common.result.ResultCode;
import com.meeleet.learn.common.security.constant.SecurityConstants;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.*;

@Configuration
@EnableAuthorizationServer
@AllArgsConstructor
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private final AuthenticationManager authenticationManager;
    private final ClientDetailsServiceImpl clientDetailsService;
    private final SysUserDetailsServiceImpl sysUserDetailsService;
    private final MemberUserDetailsServiceImpl memberUserDetailsService;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * OAuth2客户端【Dubbo服务调用】
     */
    @Override
    @SneakyThrows
    public void configure(ClientDetailsServiceConfigurer clients) {
        clients.withClientDetails(clientDetailsService);
    }


    /**
     * 配置授权（authorization）以及令牌（token）的访问端点和令牌服务(token services)
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // Token增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        //token存储模式设定 默认为InMemoryTokenStore模式存储到内存中
        endpoints.tokenStore(jwtTokenStore());

        // 获取原有默认授权模式(授权码模式、密码模式、客户端模式、简化模式)的授权者
        List<TokenGranter> granterList = new ArrayList<>(Arrays.asList(endpoints.getTokenGranter()));

        // 添加验证码授权模式授权者
        granterList.add(new CaptchaTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), authenticationManager, stringRedisTemplate
        ));

        // 添加手机短信验证码授权模式的授权者
        granterList.add(new SmsCodeTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), authenticationManager
        ));

        // 添加微信授权模式的授权者
        granterList.add(new WechatTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), authenticationManager
        ));

        CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(granterList);
        endpoints
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain)
                .tokenGranter(compositeTokenGranter)
                .tokenServices(tokenServices(endpoints));
    }

    /**
     * jwt token存储模式
     */
    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    public DefaultTokenServices tokenServices(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancerChain);

        // 多用户体系下，刷新token再次认证客户端ID和 UserDetailService 的映射Map
        Map<String, ExtUserDetailsService> clientUserDetailsServiceMap = new HashMap<>();
        clientUserDetailsServiceMap.put(SecurityConstants.ADMIN_CLIENT_ID, sysUserDetailsService); // 系统管理客户端
        clientUserDetailsServiceMap.put(SecurityConstants.APP_CLIENT_ID, memberUserDetailsService); // Android、IOS、H5 移动客户端
        clientUserDetailsServiceMap.put(SecurityConstants.WEAPP_CLIENT_ID, memberUserDetailsService); // 微信小程序客户端

        // 刷新token模式下，重写预认证提供者替换其AuthenticationManager，可自定义根据客户端ID和认证方式区分用户体系获取认证用户信息
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new PreAuthenticatedUserDetailsService<>(clientUserDetailsServiceMap));
        tokenServices.setAuthenticationManager(new ProviderManager(Arrays.asList(provider)));

        /** refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
         *  1 重复使用：access_token过期刷新时， refresh_token过期时间未改变，仍以初次生成的时间为准
         *  2 非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新便永不失效达到无需再次登录的目的
         */
        tokenServices.setReuseRefreshToken(true);
        return tokenServices;

    }

    /**
     * 使用非对称加密算法对token签名
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        return converter;
    }

    /**
     * 密钥库中获取密钥对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        KeyPair keyPair = factory.getKeyPair("jwt", "123456".toCharArray());
        return keyPair;
    }


    /**
     * JWT内容增强
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> additionalInfo = MapUtil.newHashMap();
            Object principal = authentication.getUserAuthentication().getPrincipal();
            if (principal instanceof SysUserDetails) {
                SysUserDetails sysUserDetails = (SysUserDetails) principal;
                additionalInfo.put("userId", sysUserDetails.getUserId());
                additionalInfo.put("username", sysUserDetails.getUsername());
                additionalInfo.put("deptId", sysUserDetails.getDeptId());
                // 认证身份标识(username:用户名；)
                if (StrUtil.isNotBlank(sysUserDetails.getAuthenticationIdentity())) {
                    additionalInfo.put("authenticationIdentity", sysUserDetails.getAuthenticationIdentity());
                }
            } else if (principal instanceof MemberUserDetails) {
                MemberUserDetails memberUserDetails = (MemberUserDetails) principal;
                additionalInfo.put("memberId", memberUserDetails.getMemberId());
                additionalInfo.put("username", memberUserDetails.getUsername());
                // 认证身份标识(mobile:手机号；openId:开放式认证系统唯一身份标识)
                if (StrUtil.isNotBlank(memberUserDetails.getAuthenticationIdentity())) {
                    additionalInfo.put("authenticationIdentity", memberUserDetails.getAuthenticationIdentity());
                }
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        // 客户端认证自定义异常处理
        OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint = createOAuth2AuthenticationEntryPoint();
        CustomClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter = new CustomClientCredentialsTokenEndpointFilter(security);
        // 必须首先执行这个，再继续接下来的配置
        clientCredentialsTokenEndpointFilter.afterPropertiesSet();
        clientCredentialsTokenEndpointFilter.setFilterProcessesUrl("/oauth/token");
        clientCredentialsTokenEndpointFilter.setAuthenticationEntryPoint(oAuth2AuthenticationEntryPoint);

        security.addTokenEndpointAuthenticationFilter(clientCredentialsTokenEndpointFilter);
        security.authenticationEntryPoint(oAuth2AuthenticationEntryPoint)
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()");
    }

    /**
     * 客户端认证异常响应数据(客户端认证走的是Filter)
     */
    public OAuth2AuthenticationEntryPoint createOAuth2AuthenticationEntryPoint() {
        OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint = new OAuth2AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
                Result result = Result.failed(ResultCode.CLIENT_AUTHENTICATION_FAILED);
                if (authException instanceof BadCredentialsException) {
                    result = Result.failed(ResultCode.CLIENT_ID_OR_PASSWORD_ERROR);
                }
                response.getWriter().print(JSONUtil.toJsonStr(result));
                response.getWriter().flush();
            }
        };
        oAuth2AuthenticationEntryPoint.setTypeName("Form");
        oAuth2AuthenticationEntryPoint.setRealmName("oauth2/client");
        return oAuth2AuthenticationEntryPoint;
    }


}
