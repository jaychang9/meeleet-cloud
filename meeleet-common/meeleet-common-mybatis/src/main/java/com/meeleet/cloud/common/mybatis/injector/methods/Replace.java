package com.meeleet.cloud.common.mybatis.injector.methods;

import com.meeleet.cloud.common.mybatis.injector.MeeleetSqlMethod;

/**
 * 插入一条数据（选择字段插入）
 * <p>
 * 表示插入替换数据，需求表中有PrimaryKey，或者unique索引，如果数据库已经存在数据，则用新数据替换，如果没有数据效果则和insert into一样；
 * </p>
 *
 * @author jaychang
 */
public class Replace extends AbstractInsertMethod {

	public Replace() {
		super(MeeleetSqlMethod.REPLACE_ONE);
	}
}

