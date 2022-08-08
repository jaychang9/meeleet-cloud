package com.meeleet.cloud.auth.config;

import com.meeleet.cloud.ums.rpc.IUmsMemberDubboService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class DubboImportServiceConfig {
    @DubboReference
    private IUmsMemberDubboService memberDubboService;
}
