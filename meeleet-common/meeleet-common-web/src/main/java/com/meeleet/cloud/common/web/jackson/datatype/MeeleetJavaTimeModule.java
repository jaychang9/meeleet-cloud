package com.meeleet.cloud.common.web.jackson.datatype;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 时间模型处理
 *
 * @author pangu
 */
public class MeeleetJavaTimeModule extends SimpleModule {
    /**
     * java 8 时间格式化
     */
    public static final DateTimeFormatter NORM_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
    public static final DateTimeFormatter PURE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN);

    public static final DateTimeFormatter NORM_DATE_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
    public static final DateTimeFormatter PURE_DATE_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN);

    public static final DateTimeFormatter NORM_TIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN);
    public static final DateTimeFormatter PURE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.PURE_TIME_PATTERN);

    public static final DateTimeFormatter NORM_MONTH_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN);
    public static final DateTimeFormatter SIMPLE_MONTH_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.SIMPLE_MONTH_PATTERN);

    public MeeleetJavaTimeModule() {
        super(PackageVersion.VERSION);
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(NORM_DATETIME_FORMATTER));
        this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(NORM_DATETIME_FORMATTER));
        this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(PURE_DATETIME_FORMATTER));

        this.addSerializer(LocalDate.class, new LocalDateSerializer(NORM_DATE_FORMATTER));
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(NORM_DATE_FORMATTER));
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(PURE_DATE_FORMATTER));

        this.addSerializer(LocalTime.class, new LocalTimeSerializer(NORM_TIME_FORMATTER));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(NORM_TIME_FORMATTER));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(PURE_TIME_FORMATTER));

        this.addSerializer(YearMonth.class, new YearMonthSerializer(NORM_MONTH_FORMATTER));
        this.addDeserializer(YearMonth.class, new YearMonthDeserializer(NORM_MONTH_FORMATTER));
        this.addDeserializer(YearMonth.class, new YearMonthDeserializer(SIMPLE_MONTH_FORMATTER));
    }
}
