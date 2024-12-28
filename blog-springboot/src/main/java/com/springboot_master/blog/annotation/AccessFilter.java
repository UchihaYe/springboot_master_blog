package com.springboot_master.blog.annotation;

import java.lang.annotation.*;

/**
 * @author hnz
 * @date 2022/3/23 11:16
 * @description redis接口限流
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessFilter {


    int second();


    int count();

    int filterTime();
}
