package com.meeleet.cloud.common.mybatis.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 日志打印配置
 *
 * @author jaychang
 */
@Getter
@Setter
@RefreshScope
@Component
@ConfigurationProperties("meeleet.mybatis")
public class MeeleetMybatisProperties {
	/**
	 * 是否打印可执行 sql
	 */
	private boolean sql = true;
}
