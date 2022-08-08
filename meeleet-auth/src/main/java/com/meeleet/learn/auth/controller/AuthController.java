package com.meeleet.cloud.auth.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.meeleet.cloud.common.result.Result;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import com.meeleet.cloud.common.security.util.JwtUtils;
import com.meeleet.cloud.common.security.util.RequestUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenKeyEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/oauth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final TokenEndpoint tokenEndpoint;
    private final CheckTokenEndpoint checkTokenEndpoint;
    private final TokenKeyEndpoint tokenKeyEndpoint;
    private final RedisTemplate redisTemplate;
    private final KeyPair keyPair;


    /**
     * 由于这里覆盖了org.springframework.security.oauth2.provider.endpoint.TokenEndpoint#postAccessToken(java.security.Principal, java.util.Map)
     * 所以使得org.springframework.security.oauth2.provider.endpoint.AbstractEndpoint#providerExceptionHandler 异常处理失效
     * 因此我们定义了@see com.meeleet.cloud.auth.common.exception.AuthExceptionHandler来处理认证异常
     * 但要注意的是客户端认证是通过filter的，所以不可能通过AuthExceptionHandler来处理客户端认证异常
     * @param principal
     * @param parameters
     * @return
     * @throws HttpRequestMethodNotSupportedException
     */
    @Operation(description = "OAuth2认证")
    @Parameters({
            @Parameter(name = "grant_type", schema = @Schema(defaultValue = "password"), description = "授权模式", required = true),
            @Parameter(name = "client_id", schema = @Schema(defaultValue = "client"), description = "Oauth2客户端ID", required = true),
            @Parameter(name = "client_secret", schema = @Schema(defaultValue = "123456"), description = "Oauth2客户端秘钥", required = true),
            @Parameter(name = "refresh_token", description = "刷新token"),
            @Parameter(name = "username", schema = @Schema(defaultValue = "admin"), description = "用户名"),
            @Parameter(name = "password", schema = @Schema(defaultValue = "123456"), description = "用户密码")
    })
    @PostMapping("/token")
    public Result<OAuth2AccessToken> postAccessToken(
            @Parameter(hidden = true) Principal principal,
            @Parameter(hidden = true) @RequestParam Map<String, String> parameters
    ) throws HttpRequestMethodNotSupportedException {

        /**
         * 获取登录认证的客户端ID
         *
         * 兼容两种方式获取Oauth2客户端信息（client_id、client_secret）
         * 方式一：client_id、client_secret放在请求路径中(注：当前版本已废弃)
         * 方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
         */
        RequestUtils.ClientAuthRequest clientAuthRequest = RequestUtils.getOAuth2ClientAuthRequest();
        if (log.isDebugEnabled()) {
            HashMap<String, String> debugParameters = new HashMap<>(parameters);
            debugParameters.put("password", "******");
            log.debug("OAuth认证授权 客户端ID:{}，请求参数：{}", clientAuthRequest.getClientId(), JSONUtil.toJsonStr(debugParameters));
        }

        parameters.putIfAbsent(SecurityConstants.CLIENT_ID_KEY,clientAuthRequest.getClientId());
        parameters.putIfAbsent(SecurityConstants.CLIENT_SECRET_KEY,clientAuthRequest.getClientSecret());

        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        return Result.success(accessToken);
    }

    @Operation(description = "校验Token")
    @Parameters({
            @Parameter(name = "Authorization", description = "客户端Basic认证: base64(client_id:client_secret)", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "token", description = "访问令牌(accessToken)", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "string")),
    })
    @PostMapping("/check_token")
    public Result checkToken(@RequestParam("token") String token) {
        Map<String, ?> checkResult = checkTokenEndpoint.checkToken(token);
        return Result.success(checkResult);
    }

    @Operation(description = "获取Token公钥")
    @Parameters({
            @Parameter(name = "Authorization", description = "客户端Basic认证: base64(client_id:client_secret)", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
    })
    @GetMapping("/token_key")
    public Result tokenKey() {
        Map<String, String> tokenKeyResult = tokenKeyEndpoint.getKey(null);
        return Result.success(tokenKeyResult);
    }

    @Operation(description = "注销")
    @PostMapping("/logout")
    public Result logout() {
        // 必须网关转发到这个接口，因为网关全局过滤器会将jti塞到请求头里
        JSONObject payload = JwtUtils.getJwtPayload();
        String jti = payload.getStr(SecurityConstants.JWT_JTI); // JWT唯一标识
        Long expireTime = payload.getLong(SecurityConstants.JWT_EXP); // JWT过期时间戳(单位：秒)
        if (expireTime != null) {
            long currentTime = System.currentTimeMillis() / 1000;// 当前时间（单位：秒）
            if (expireTime > currentTime) { // token未过期，添加至缓存作为黑名单限制访问，缓存时间为token过期剩余时间
                redisTemplate.opsForValue().set(SecurityConstants.TOKEN_BLACKLIST_PREFIX + jti, null, (expireTime - currentTime), TimeUnit.SECONDS);
            }
        } else { // token 永不过期则永久加入黑名单
            redisTemplate.opsForValue().set(SecurityConstants.TOKEN_BLACKLIST_PREFIX + jti, null);
        }
        return Result.success("注销成功");
    }

//    网关项目本地取公钥
//    @ApiOperation(value = "获取公钥")
//    @GetMapping("/public-key")
//    public Map<String, Object> getPublicKey() {
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAKey key = new RSAKey.Builder(publicKey).build();
//        return new JWKSet(key).toJSONObject();
//    }

}
