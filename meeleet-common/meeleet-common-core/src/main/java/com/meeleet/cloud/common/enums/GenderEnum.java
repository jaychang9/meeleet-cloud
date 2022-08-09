package com.meeleet.cloud.common.enums;

import com.meeleet.cloud.common.validation.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 性别枚举
 *
 * @author jaychang
 */
@Getter
@AllArgsConstructor
public enum GenderEnum implements IBaseEnum<Integer>, IntArrayValuable {

    MALE(1, "男"),
    FEMALE(2, "女"),
    UNKNOWN(0, "未知");


    // @EnumValue //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    private final Integer value;

    // @JsonValue //  表示对枚举序列化时返回此字段
    private final String label;

    /**
     * 值数组
     */
    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(GenderEnum::getValue).toArray();

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
