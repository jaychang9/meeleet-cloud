package com.meeleet.cloud.common.pojo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huawei
 * @desc 基础分页请求对象
 * @email huawei_code@163.com
 * @date 2021/2/28
 */
@Data
@Schema
public class BasePageQuery implements Serializable {

    private static final long serialVersionUID = -5602934649129609232L;

    @Schema(description = "页码", example = "1")
    private int current = 1;

    @Schema(description = "每页记录数", example = "10")
    private int size = 10;
}
