package com.meeleet.cloud.common.auth.security.extension.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailServiceFactory;
import com.meeleet.cloud.common.auth.security.userdetails.ExtUserDetailsService;
import com.meeleet.cloud.common.auth.security.userdetails.PreAuthenticationChecks;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 微信认证提供者
 *
 * @author <a href="mailto:jaychagn1987@gmail.com">jaychang</a>
 * @date 2022/08/01
 */
public class WechatAuthenticationProvider implements AuthenticationProvider {

    @Setter
    private ExtUserDetailServiceFactory extUserDetailServiceFactory;

    @Setter
    private WxMaService wxMaService;

    private PreAuthenticationChecks preAuthenticationChecks = new PreAuthenticationChecks();

    /**
     * 微信认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WechatAuthenticationToken authenticationToken = (WechatAuthenticationToken) authentication;
        String code = (String) authenticationToken.getPrincipal();

        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        String openid = sessionInfo.getOpenid();

        Map<String, String> parameters = (Map<String, String>) authentication.getDetails();
        String clientId = parameters.get(SecurityConstants.CLIENT_ID_KEY);
        ExtUserDetailsService userDetailsService = this.getUserDetailsService(clientId);

        Assert.notNull(userDetailsService, String.format("UserDetailsService must not null,please check whether userDetailsService corresponding to client_id:%s exists.", clientId));
        UserDetails loadedUser = userDetailsService.loadUserByOpenid(openid);

        // TODO 想办法移除addUser方法
        // 微信用户不存在，注册成为新会员
        if (Objects.isNull(loadedUser)) {
            String sessionKey = sessionInfo.getSessionKey();
            String encryptedData = authenticationToken.getEncryptedData();
            String iv = authenticationToken.getIv();
            // 解密 encryptedData 获取用户信息
            WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
            userDetailsService.addUser(BeanUtil.beanToMap(userInfo));
        }
        loadedUser = userDetailsService.loadUserByOpenid(openid);

        preAuthenticationChecks.check(loadedUser);

        WechatAuthenticationToken result = new WechatAuthenticationToken(loadedUser, Optional.ofNullable(loadedUser.getAuthorities()).orElse(new HashSet<>()));
        result.setDetails(authentication.getDetails());
        return result;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return WechatAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public ExtUserDetailsService getUserDetailsService(String clientId) {
        return this.extUserDetailServiceFactory.getService(clientId);
    }
}
