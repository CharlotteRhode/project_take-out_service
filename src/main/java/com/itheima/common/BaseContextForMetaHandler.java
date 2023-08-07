package com.itheima.common;

import org.apache.catalina.webresources.TomcatJarInputStream;

import java.lang.invoke.VarHandle;





/*为了公共字段自动填充MetaObjectHandler- ThreadLocal 而设置的工具类：
用于保存和动态获取当前用户的userId*/

public class BaseContextForMetaHandler {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();

    }

}
