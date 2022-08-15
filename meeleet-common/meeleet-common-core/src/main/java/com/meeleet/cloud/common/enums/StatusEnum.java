package com.meeleet.cloud.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 公共状态，一般用来表示开启和关闭
 *
 * @author aaronuu
 */
@Getter
@AllArgsConstructor
public enum StatusEnum implements IBaseEnum<String> {

    /**
     * 启用
     */
    ENABLE("enable", "启用"),

    /**
     * 禁用
     */
    DISABLE("disable", "禁用");

    private final String value;

    private final String label;

    /**
     * 根据code获取枚举
     */
    public static StatusEnum valueToEnum(String value) {
        Optional<StatusEnum> enumOptional = Arrays.stream(StatusEnum.values()).filter(e -> e.getValue().equals(value)).findFirst();
        return enumOptional.isPresent() ? enumOptional.get() : null;
    }
}
