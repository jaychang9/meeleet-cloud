package com.meeleet.learn.auth.config;

import com.meeleet.learn.ums.rpc.IUmsMemberDubboService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class DubboImportServiceConfig {
    @DubboReference
    private IUmsMemberDubboService memberDubboService;
}
