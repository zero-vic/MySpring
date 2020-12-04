package com.xy.myspring.bean.utils;

import com.xy.myspring.bean.annotation.Component;
import com.xy.myspring.bean.annotation.Controller;
import com.xy.myspring.bean.annotation.Repository;
import com.xy.myspring.bean.annotation.Service;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created With IntelliJ IDEA
 * User: yao
 * Date:2020/12/03
 * Description:
 * Version:V1.0
 */
public class AnnotationList {


    public static List<Class<? extends Annotation>> getAnnotationList() {
        List<Class<? extends Annotation>> list = Arrays.asList(Controller.class, Service.class, Component.class, Repository.class);

        return list;
    }
}

