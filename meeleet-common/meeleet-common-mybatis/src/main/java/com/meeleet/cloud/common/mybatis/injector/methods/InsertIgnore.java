package com.meeleet.cloud.common.mybatis.injector.methods;


import com.meeleet.cloud.common.mybatis.injector.MeeleetSqlMethod;

/**
 * 插入一条数据（选择字段插入）插入如果中已经存在相同的记录，则忽略当前新数据
 *
 * @author jaychang
 */
public class InsertIgnore extends AbstractInsertMethod {

	public InsertIgnore() {
		super(MeeleetSqlMethod.INSERT_IGNORE_ONE);
	}
}
