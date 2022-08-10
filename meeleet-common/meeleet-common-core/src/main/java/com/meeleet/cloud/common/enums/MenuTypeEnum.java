package com.meeleet.cloud.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.meeleet.cloud.common.validation.IntArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 菜单类型枚举
 *
 * @author jaychang
 */

public enum MenuTypeEnum implements IBaseEnum<Integer>, IntArrayValuable {

    NULL(0, null),
    MENU(1, "菜单"),
    CATALOG(2, "目录"),
    EXT_LINK(3, "外链");

    @Getter
    @EnumValue //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    private Integer value;

    @Getter
    // @JsonValue //  表示对枚举序列化时返回此字段
    private String label;

    MenuTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(MenuTypeEnum::getValue).toArray();

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
