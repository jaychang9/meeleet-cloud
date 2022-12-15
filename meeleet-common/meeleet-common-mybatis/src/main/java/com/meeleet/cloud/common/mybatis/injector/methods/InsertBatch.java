package com.meeleet.cloud.common.mybatis.injector.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;

/**
 * 批量插入数据
 *
 * @author jaychang
 */
public class InsertBatch extends AbstractInsertBatch {
	private static final String SQL_METHOD = "insertBatch";

	public InsertBatch() {
		super(SqlMethod.INSERT_ONE.getSql(), SQL_METHOD);
	}
}
