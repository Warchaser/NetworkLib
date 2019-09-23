package com.warchaser.networklib.provider;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 返回体格式标签
 * */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ResponseFormat {

    String JSON = "json";

    String XML = "xml";

    String value() default "";
}
