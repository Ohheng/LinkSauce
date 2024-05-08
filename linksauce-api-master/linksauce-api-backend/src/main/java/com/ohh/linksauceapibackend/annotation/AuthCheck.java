package com.ohh.linksauceapibackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Ohh
 * @Desctription: 权限校验
 * @Date: 2024-05-09 00:11
 * @Version: 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 有任何一个角色
     *
     * @return {@link String[]}
     */
    String[] anyRole() default "";

    /**
     * 必须有某个角色
     *
     * @return {@link String}
     */
    String mustRole() default "";
}
