package com.meeleet.cloud.gateway.security;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import com.meeleet.cloud.common.result.ResultCode;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import com.meeleet.cloud.gateway.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

/**
 * 资源服务器配置
 *
 * @author jaychang
 * @date 2020/05/01
 */
@ConfigurationProperties(prefix = "security")
@RequiredArgsConstructor
@Configuration
@EnableWebFluxSecurity
@Slf4j
public class ResourceServerConfig {

    private final ResourceServerManager resourceServerManager;

    @Setter
    private List<String> ignoreUrls;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        if (ignoreUrls == null) {
            log.error("网关白名单路径读取失败：Nacos配置读取失败，请检查配置中心连接是否正确！");
        }

        http
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                .publicKey(rsaPublicKey())   // 本地加载公钥
                //.jwkSetUri()  // 远程获取公钥，默认读取的key是spring.security.oauth2.resourceserver.jwt.jwk-set-uri
        ;
        http.oauth2ResourceServer().authenticationEntryPoint(authenticationEntryPoint());
        http.authorizeExchange()
                .pathMatchers(Convert.toStrArray(ignoreUrls)).permitAll()
                .anyExchange().access(resourceServerManager)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler()) // 处理未授权
                .authenticationEntryPoint(authenticationEntryPoint()) //处理未认证
                .and().csrf().disable();

        return http.build();
    }

    /**
     * 自定义未授权响应
     */
    @Bean
    ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            Mono<Void> mono = Mono.defer(() -> Mono.just(exchange.getResponse()))
                    .flatMap(response -> ResponseUtils.writeErrorInfo(response, ResultCode.ACCESS_UNAUTHORIZED));
            return mono;
        };
    }

    /**
     * token无效或者已过期自定义响应
     */
    @Bean
    ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, e) -> {
            Mono<Void> mono = Mono.defer(() -> Mono.just(exchange.getResponse()))
                    .flatMap(response -> ResponseUtils.writeErrorInfo(response, ResultCode.TOKEN_INVALID_OR_EXPIRED));
            return mono;
        };
    }

    /**
     * @link https://blog.csdn.net/qq_24230139/article/details/105091273
     * ServerHttpSecurity没有将jwt中authorities的负载部分当做Authentication
     * 需要把jwt的Claim中的authorities加入
     * 方案：重新定义权限管理器，默认转换器JwtGrantedAuthoritiesConverter
     */
    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(SecurityConstants.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(SecurityConstants.JWT_AUTHORITIES_KEY);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    /**
     * 本地获取JWT验签公钥
     */
    @SneakyThrows
    @Bean
    public RSAPublicKey rsaPublicKey() {

        // return RsaKeyConverters.x509().convert(new ClassPathResource("public.key").getInputStream());
        // 以上代码看了下好像没关闭流故还是用以下代码

        Resource resource = new ClassPathResource("public.key");
        InputStream is = resource.getInputStream();
        String publicKeyData = IoUtil.read(is).toString();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec((Base64.decode(publicKeyData)));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        return rsaPublicKey;
    }

}
