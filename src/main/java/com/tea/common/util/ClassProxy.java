package com.tea.common.util;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by MegaX on 2017/1/11.
 */
public class ClassProxy {
    public static InvocationHandler getInvocationHandler(Object proxy) {
        if(proxy == null) return null;
        if(!isProxyClass(proxy.getClass()))
        {
            throw new RuntimeException("proxy is not a proxy class");
        }
        try
        {
            Method method = Enhancer.class.getDeclaredMethod("getCallbackField",int.class);
            method.setAccessible(true);
            String fieldName = (String) method.invoke(null,0);
            Field f =  proxy.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object iv = f.get(proxy);
            if(!(iv instanceof InvocationHandler))
            {
                throw new RuntimeException("proxy is not a create by self");
            }
            return (InvocationHandler)iv;
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
    public static boolean isProxyClass(Class cl) {
        return  (Enhancer.isEnhanced(cl));
    }

    public static <T> T newProxyInstance(Class<T> clazz, InvocationHandler h) {
        if(Enhancer.isEnhanced(clazz))
        {
            return null;
        }
        return (T) Enhancer.create(clazz,h);
    }
}
