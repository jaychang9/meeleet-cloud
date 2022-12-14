package com.meeleet.cloud.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 字段自动填充
 *
 * @link https://mp.baomidou.com/guide/auto-fill-metainfo.html
 */
@Component
public class MeeleetMetaObjectHandler implements MetaObjectHandler {

    public static final String CREATE_FIELD_NAME = "createTime";

    public static final String UPDATE_FIELD_NAME = "updateTime";

    /**
     * 新增填充创建时间
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (this.getFieldValByName(CREATE_FIELD_NAME, metaObject) == null) {
            this.strictInsertFill(metaObject, CREATE_FIELD_NAME, () -> LocalDateTime.now(), LocalDateTime.class);
        }
        if (this.getFieldValByName(UPDATE_FIELD_NAME, metaObject) == null) {
            this.strictUpdateFill(metaObject, UPDATE_FIELD_NAME, () -> LocalDateTime.now(), LocalDateTime.class);
        }

    }

    /**
     * 更新填充更新时间
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (this.getFieldValByName(UPDATE_FIELD_NAME, metaObject) == null) {
            this.strictUpdateFill(metaObject, UPDATE_FIELD_NAME, () -> LocalDateTime.now(), LocalDateTime.class);
        }
    }

}
