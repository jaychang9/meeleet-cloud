package com.meeleet.cloud.common.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.meeleet.cloud.common.web.jackson.datatype.MeeleetJavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 序列化和反序列化配置
 *
 * @author pangu
 * @link https://www.cnblogs.com/asker009/p/12888388.html
 */
@Configuration
public class JacksonConfig {

	@Value("${spring.jackson.time-zone:GMT+8}")
	private String timeZone;
	@Value("${spring.jackson.locale:zh_CN}")
	private String locale;

	/**
	 * Jackson全局转化long类型为String，解决jackson序列化时传入前端Long类型缺失精度问题
	 * Jackson日期格式序列化，反序列化
	 */
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return jacksonObjectMapperBuilder -> {
			jacksonObjectMapperBuilder.timeZone(timeZone);
			jacksonObjectMapperBuilder.locale(locale);
			/**
			 * 这个特性决定parser是否将允许使用非双引号属性名字， （这种形式在Javascript中被允许，但是JSON标准说明书中没有）。
			 *
			 * 注意：由于JSON标准上需要为属性名称使用双引号，所以这也是一个非标准特性，默认是false的。
			 * 同样，需要设置JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES为true，打开该特性。
			 *
			 */
			jacksonObjectMapperBuilder.featuresToEnable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

			jacksonObjectMapperBuilder.serializerByType(BigInteger.class, ToStringSerializer.instance);
			jacksonObjectMapperBuilder.serializerByType(BigDecimal.class, ToStringSerializer.instance);
			jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);

			jacksonObjectMapperBuilder.modulesToInstall(MeeleetJavaTimeModule.class);
		};
	}
}
