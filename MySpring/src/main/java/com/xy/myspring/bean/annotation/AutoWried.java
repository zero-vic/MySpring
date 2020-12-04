package com.xy.myspring.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created With IntelliJ IDEA
 * User: yao
 * Date:2020/12/03
 * Description:用于依赖注入的注解
 * Version:V1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoWried {
}
