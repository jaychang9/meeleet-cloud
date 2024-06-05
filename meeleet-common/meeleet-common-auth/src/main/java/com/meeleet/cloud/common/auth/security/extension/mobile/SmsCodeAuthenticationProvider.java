package com.meeleet.cloud.common.auth.security.extension.mobile;

import cn.hutool.core.util.StrUtil;
import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailServiceFactory;
import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailsService;
import com.meeleet.cloud.common.exception.BusinessException;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import com.meeleet.cloud.common.util.StringPool;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * 短信验证码认证授权提供者
 *
 * @author <a href="mailto:jaychang1987@gmail.com">jaychang</a>
 * @date 2022/08/01
 */
public class SmsCodeAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    @Setter
    private ExtUserDetailServiceFactory extUserDetailServiceFactory;
    private StringRedisTemplate redisTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        String mobile = (String) authenticationToken.getPrincipal();
        String code = (String) authenticationToken.getCredentials();
        Map<String, String> parameters = (Map<String, String>) authentication.getDetails();
        String clientId = parameters.get(SecurityConstants.CLIENT_ID_KEY);
        if (!code.equals("666666")) { // 666666 是后门，因为短信收费，正式环境删除这个if分支
            String codeKey = SecurityConstants.AUTH_SMS_CODE_PREFIX + clientId + StringPool.COLON + mobile;
            String correctCode = redisTemplate.opsForValue().get(codeKey);
            // 验证码比对
            if (StrUtil.isBlank(correctCode) || !code.equals(correctCode)) {
                throw new BusinessException("验证码不正确");
            }
            // 比对成功删除缓存的验证码
            redisTemplate.delete(codeKey);
        }

        ExtUserDetailsService userDetailsService = getUserDetailsService(clientId);
        Assert.notNull(userDetailsService, String.format("UserDetailsService must not null,please check whether userDetailsService corresponding to client_id:%s exists.", clientId));
        UserDetails loadedUser = userDetailsService.loadUserByMobile(mobile);
        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }

        SmsCodeAuthenticationToken result = new SmsCodeAuthenticationToken(loadedUser, authentication.getCredentials(), Optional.ofNullable(loadedUser.getAuthorities()).orElse(new HashSet<>()));
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.extUserDetailServiceFactory, "A ExtUserDetailServiceFactory must be set");
    }

    public ExtUserDetailsService getUserDetailsService(String clientId) {
        return extUserDetailServiceFactory.getService(clientId);
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
