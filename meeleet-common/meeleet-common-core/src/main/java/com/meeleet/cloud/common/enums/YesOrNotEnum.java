package com.meeleet.cloud.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是或否的枚举，一般用在数据库字段，例如del_flag字段，char(1)，填写Y或N
 *
 * @author aaronuu
 */
@Getter
@AllArgsConstructor
public enum YesOrNotEnum implements IBaseEnum<String>{

    /**
     * 是
     */
    Y("Y", "是"),

    /**
     * 否
     */
    N("N", "否");

    private final String value;
    private final String label;
}
