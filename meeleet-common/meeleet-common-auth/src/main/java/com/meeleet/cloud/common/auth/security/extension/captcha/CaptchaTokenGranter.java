package com.meeleet.cloud.common.auth.security.extension.captcha;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.meeleet.cloud.common.constant.StringConstant;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 图片验证码授权模式授权者
 *
 * @author jaychang
 * @date 2022/08/01
 */
public class CaptchaTokenGranter extends AbstractTokenGranter {

    /**
     * 声明授权者 CaptchaTokenGranter 支持授权模式 captcha
     * 根据接口传值 grant_type = captcha 的值匹配到此授权者
     * 匹配逻辑详见下面的两个方法
     *
     * @see CompositeTokenGranter#grant(String, TokenRequest)
     * @see AbstractTokenGranter#grant(String, TokenRequest)
     */
    private static final String GRANT_TYPE = "captcha";
    private final AuthenticationManager authenticationManager;
    private StringRedisTemplate redisTemplate;

    public CaptchaTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                               OAuth2RequestFactory requestFactory, AuthenticationManager authenticationManager,
                               StringRedisTemplate redisTemplate
    ) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap(tokenRequest.getRequestParameters());

        // 验证码校验逻辑
        String validateCode = parameters.get("code");
        String uuid = parameters.get("uuid");

        Assert.isTrue(StrUtil.isNotBlank(validateCode), "验证码不能为空");
        String clientId = parameters.get(SecurityConstants.CLIENT_ID_KEY);
        String validateCodeKey = SecurityConstants.AUTH_VALIDATION_CODE_KEY_PREFIX + clientId + StringConstant.COLON_SPLIT_STR + uuid;

        // 从缓存取出正确的验证码和用户输入的验证码比对
        String correctValidateCode = redisTemplate.opsForValue().get(validateCodeKey);
        Assert.isTrue(StrUtil.isNotBlank(correctValidateCode), "验证码已过期");
        Assert.isTrue(validateCode.equals(correctValidateCode), "您输入的验证码不正确");

        // 验证码验证通过，删除 Redis 的验证码
        redisTemplate.delete(validateCodeKey);

        String username = parameters.get("username");
        String password = parameters.get("password");

        // 移除后续无用参数
        parameters.remove("password");
        parameters.remove("code");
        parameters.remove("uuid");

        // 和密码模式一样的逻辑
        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        } catch (BadCredentialsException e) {
            // If the username/password are wrong the spec says we should send 400/invalid grant
            throw new InvalidGrantException(e.getMessage());
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
