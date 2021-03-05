package com.manager.traffic.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author a
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    /**
     * 路由的uri
     *
     * @return
     */
    String uri();

    /**
     * 路由的方法
     *
     * @return
     */
    String method();
}
