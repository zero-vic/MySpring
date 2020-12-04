package com.xy.test.service;

import com.xy.myspring.bean.annotation.Service;

/**
 * Created With IntelliJ IDEA
 * User: yao
 * Date:2020/12/03
 * Description:
 * Version:V1.0
 */
@Service
public class UserService {

    public String hello(){
        System.out.println("service.......");
        return "hello";
    }
}
