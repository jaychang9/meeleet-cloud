package com.meeleet.cloud.common.proxy;


import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;

class ProxyCreatorTest {

    public static interface Flyable {
        void fly();
    }

    @Test
    void getProxy() {
        Flyable proxy = ProxyCreator.getProxy(Flyable.class, new ObjectFactory<Flyable>() {
            @Override
            public Flyable getObject() throws BeansException {
                System.out.println("create a fly object");
                return () -> System.out.println("flying...");
            }
        });

        proxy.fly();
    }
}