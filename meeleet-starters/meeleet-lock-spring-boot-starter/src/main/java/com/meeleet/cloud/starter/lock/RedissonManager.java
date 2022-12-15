package com.meeleet.cloud.starter.lock;

import com.google.common.base.Preconditions;
import com.meeleet.cloud.starter.lock.config.strategy.*;
import com.meeleet.cloud.starter.lock.constant.RedisConnectionType;
import com.meeleet.cloud.starter.lock.props.RedissonProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * Redisson核心配置，用于提供初始化的redisson实例
 *
 * @author pangu
 * @date 2020-10-22
 */
@Slf4j
public class RedissonManager {

	private Config config = new Config();

	private RedissonClient redisson = null;

	public RedissonManager() {
	}

	public RedissonManager(RedissonProperties redissonProperties) {
		try {
			config = RedissonConfigFactory.getInstance().createConfig(redissonProperties);
			redisson =  Redisson.create(config);
		} catch (Exception e) {
			log.error("Redisson init error", e);
			throw new IllegalArgumentException("please input correct configurations," +
					"connectionType must in standalone/sentinel/cluster/masterslave");
		}
	}

	public RedissonClient getRedisson() {
		return redisson;
	}

	/**
	 * Redisson连接方式配置工厂
	 * 双重检查锁
	 */
	static class RedissonConfigFactory {

		private RedissonConfigFactory() {
		}

		private static volatile RedissonConfigFactory factory = null;

		public static RedissonConfigFactory getInstance() {
			if (factory == null) {
				synchronized (Object.class) {
					if (factory == null) {
						factory = new RedissonConfigFactory();
					}
				}
			}
			return factory;
		}

		private Config config = new Config();

		/**
		 * 根据连接类型获取对应连接方式的配置,基于策略模式
		 *
		 * @param redissonProperties redisson配置
		 * @return Config
		 */
		Config createConfig(RedissonProperties redissonProperties) {
			Preconditions.checkNotNull(redissonProperties);
			Preconditions.checkNotNull(redissonProperties.getAddress(), "redisson.lock.server.address cannot be NULL!");
			// Preconditions.checkNotNull(redissonProperties.getType(), "redisson.lock.server.password cannot be NULL");
			String connectionType = redissonProperties.getType();
			// 声明配置上下文
			RedissonConfigContext redissonConfigContext;
			if (connectionType.equals(RedisConnectionType.STANDALONE.getConnection_type())) {
				redissonConfigContext = new RedissonConfigContext(new StandaloneRedissonConfigStrategyImpl());
			} else if (connectionType.equals(RedisConnectionType.SENTINEL.getConnection_type())) {
				redissonConfigContext = new RedissonConfigContext(new SentinelRedissonConfigStrategyImpl());
			} else if (connectionType.equals(RedisConnectionType.CLUSTER.getConnection_type())) {
				redissonConfigContext = new RedissonConfigContext(new ClusterRedissonConfigStrategyImpl());
			} else if (connectionType.equals(RedisConnectionType.MASTERSLAVE.getConnection_type())) {
				redissonConfigContext = new RedissonConfigContext(new MasterSlaveRedissonConfigStrategyImpl());
			} else {
				throw new IllegalArgumentException("创建Redisson连接Config失败！当前连接方式:" + connectionType);
			}
			return redissonConfigContext.createRedissonConfig(redissonProperties);
		}
	}


}
