package com.meeleet.cloud.common.security.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import com.meeleet.cloud.common.security.enums.AuthenticationIdentityEnum;
import com.nimbusds.jose.JWSObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 请求工具类
 *
 * @author jaychang
 */
@Slf4j
public class RequestUtils {

    public static final String AUTHENTICATION_SCHEME_BASIC = "Basic";

    @SneakyThrows
    public static String getGrantType() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String grantType = request.getParameter(SecurityConstants.GRANT_TYPE_KEY);
        return grantType;
    }

    /**
     * 获取登录认证的客户端ID
     * <p>
     * 兼容两种方式获取OAuth2客户端信息（client_id、client_secret）
     * 方式一：client_id、client_secret放在请求路径中
     * 方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
     *
     * @return
     */
    @SneakyThrows
    public static String getOAuth2ClientId() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 从请求路径中获取
        String clientId = request.getParameter(SecurityConstants.CLIENT_ID_KEY);
        if (StrUtil.isNotBlank(clientId)) {
            return clientId;
        }

        // 从请求头获取
        String basic = request.getHeader(SecurityConstants.AUTHORIZATION_KEY);
        if (StrUtil.isNotBlank(basic) && basic.startsWith(SecurityConstants.BASIC_PREFIX)) {
            basic = basic.replace(SecurityConstants.BASIC_PREFIX, StringConstant.EMPTY_STR);
            String basicPlainText = new String(Base64.getDecoder().decode(basic.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            clientId = basicPlainText.split(":")[0]; //client:secret
        }
        return clientId;
    }

    /**
     * 获取登录认证的客户端ID
     * <p>
     * 兼容两种方式获取OAuth2客户端信息（client_id、client_secret）
     * 方式一：client_id、client_secret放在请求路径中
     * 方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
     *
     * @return
     */
    @SneakyThrows
    public static ClientAuthRequest getOAuth2ClientAuthRequest() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 从请求路径中获取
        String clientId = request.getParameter(SecurityConstants.CLIENT_ID_KEY);
        String clientSecret = request.getParameter(SecurityConstants.CLIENT_SECRET_KEY);
        if (StrUtil.isNotBlank(clientId) && StrUtil.isNotBlank(clientSecret)) {
            return new ClientAuthRequest().setClientId(clientId).setClientSecret(clientSecret);
        }

        ClientAuthRequest emptyClientAuthRequest = new ClientAuthRequest().setClientId("").setClientSecret("");

        // 从请求头获取
        String header = request.getHeader(SecurityConstants.AUTHORIZATION_KEY);
        if (header == null) {
            return emptyClientAuthRequest;
        }
        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
            return emptyClientAuthRequest;
        }
        if (header.equalsIgnoreCase(AUTHENTICATION_SCHEME_BASIC)) {
            return emptyClientAuthRequest;
        }

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        String basicPlainText = new String(Base64.getDecoder().decode(base64Token));

        String[] split = basicPlainText.split(":");
        if (ArrayUtil.isAllNull(split) || ArrayUtil.length(split) < 2) {
            return emptyClientAuthRequest;
        }
        clientId = split[0]; //client:secret
        clientSecret = split[1]; //client:secret
        return emptyClientAuthRequest.setClientId(clientId).setClientSecret(clientSecret);
    }

    /**
     * 解析JWT获取获取认证身份标识
     *
     * @return
     */
    @SneakyThrows
    public static String getAuthenticationIdentity() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String refreshToken = request.getParameter(SecurityConstants.REFRESH_TOKEN_KEY);

        String payload = StrUtil.toString(JWSObject.parse(refreshToken).getPayload());
        JSONObject jsonObject = JSONUtil.parseObj(payload);

        String authenticationIdentity = jsonObject.getStr(SecurityConstants.AUTHENTICATION_IDENTITY_KEY);
        if (StrUtil.isBlank(authenticationIdentity)) {
            authenticationIdentity = AuthenticationIdentityEnum.USERNAME.getValue();
        }
        return authenticationIdentity;
    }

    @Data
    @Accessors(chain = true)
    public static class ClientAuthRequest implements Serializable {
        private String clientId;
        private String clientSecret;
    }
}
