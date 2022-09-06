package com.meeleet.cloud.common.mybatis.handler;

import com.meeleet.cloud.common.enums.IBaseEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseEnumTypeHandler<E extends Enum<E> & IBaseEnum<Integer>> extends BaseTypeHandler<E> {

    private final Class<E> type;

    public BaseEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, e.getValue());
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return valueOf(resultSet.getInt(s));
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return valueOf(resultSet.getInt(i));
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return valueOf(callableStatement.getInt(i));
    }

    private E valueOf(int value) {
        E[] enumArray = this.type.getEnumConstants();
        for (E e : enumArray) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return null;
    }
}
