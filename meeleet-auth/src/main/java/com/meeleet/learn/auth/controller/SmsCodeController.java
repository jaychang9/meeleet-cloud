package com.meeleet.cloud.auth.controller;

import cn.hutool.core.util.RandomUtil;
import com.meeleet.cloud.common.constant.StringConstant;
import com.meeleet.cloud.common.result.Result;
import com.meeleet.cloud.common.security.constant.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sms-code")
@RequiredArgsConstructor
public class SmsCodeController {

    // TODO 发送短信服务
    private final StringRedisTemplate redisTemplate;

    @Operation(description = "发送短信验证码")
    @Parameters({
            @Parameter(name = "mobile", example = "13333333333", description = "手机号", required = true),
            @Parameter(name = "client_id", example = "client", description = "客户端ID", required = true),
    })
    @PostMapping("/send")
    public Result sendSmsCode(@RequestParam("mobile") @NotBlank(message = "手机号码不能为空") String mobile, @RequestParam("client_id") String clientId) {
        System.out.println(String.format("给手机号：%s发送登录短信验证码", mobile));
        String code = RandomUtil.randomNumbers(6); // 随机生成6位的验证码
        String key = SecurityConstants.AUTH_SMS_CODE_PREFIX + clientId + StringConstant.COLON_SPLIT_STR + mobile;
        redisTemplate.opsForValue().set(key, code, 600, TimeUnit.SECONDS);
        return Result.success("发送成功");
    }
}
