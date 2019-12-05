package com.vike.bridge.common;

import java.lang.annotation.*;

/**
 * @author: lsl
 * @createDate: 2019/12/5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiPointcut {

    String value();

}
