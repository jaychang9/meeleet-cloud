package com.meeleet.cloud.common.exception.assertion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.junit.Test;

/**
 * @author sprainkle
 * @date 2020/6/20
 */
public class AssertTest {

    @Test
    public void assertNotNull1() {
        Goods goods = new Goods().setId("7001").setName("我是商品7001");
        ResponseEnum.ORDER_CREATION_FAILED.assertNotNull(goods);

        // others
    }

    @Test
    public void assertNotNull2() {
        String goodsId = "1001";
        Goods goods = getGoods(goodsId);
        ResponseEnum.ORDER_CREATION_FAILED.assertNotNullWithMsg(goods, "商品[{0}]不存在", goodsId);

        // others
    }

    @Getter
    @AllArgsConstructor
    public enum ResponseEnum implements BusinessExceptionAssert {

        ORDER_CREATION_FAILED("7001", "订单创建失败，请稍后重试");

        private final String code;
        private final String message;
    }

    public Goods getGoods(String id) {
        return null;
    }

    @Data
    @Accessors(chain = true)
    static class Goods {
        String id;
        String name;
    }

}
