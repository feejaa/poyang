package org.feejaa.poyang.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock service proxy
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());

        // TODO: 2022/5/26
        return getDefaultValue(returnType);
    }

    private Object getDefaultValue(Class<?> returnType) {

        if (returnType.isPrimitive()) {
            if (returnType == int.class) {
                return 0;
            } else if (returnType == long.class) {
                return 0L;
            } else if (returnType == boolean.class) {
                return false;
            } else if (returnType == double.class) {
                return 0.0;
            } else if (returnType == float.class) {
                return 0.0f;
            } else if (returnType == short.class) {
                return (short) 0;
            } else if (returnType == byte.class) {
                return (byte) 0;
            } else if (returnType == char.class)
                return (char) 0;
        }
        return null;

    }

}
