package com.meeleet.cloud.common.security.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * JWT工具类
 *
 * @author jaychang
 */
@Slf4j
public class JwtUtils {

    @SneakyThrows
    public static JSONObject getJwtPayload() {
        JSONObject jsonObject = null;
        String payload = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(SecurityConstants.JWT_PAYLOAD_KEY);
        if (StrUtil.isNotBlank(payload)) {
            jsonObject = JSONUtil.parseObj(URLDecoder.decode(payload, StandardCharsets.UTF_8.name()));
        }
        return jsonObject;
    }
}
