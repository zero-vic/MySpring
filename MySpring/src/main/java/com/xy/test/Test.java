package com.xy.test;

import com.xy.myspring.bean.annotation.AutoWried;
import com.xy.myspring.bean.annotation.Controller;
import com.xy.myspring.bean.context.WebApplicationContext;
import com.xy.test.controller.UserController;

import java.net.URL;

/**
 * Created With IntelliJ IDEA
 * User: yao
 * Date:2020/12/03
 * Description:
 * Version:V1.0
 */

public class Test {

    public static void main(String[] args) {
        WebApplicationContext webApplicationContext  = new WebApplicationContext("classpath:application.xml");
        UserController userController = (UserController) webApplicationContext.getBeanByName("userController");
        System.out.println(userController);
//        URL url = test.class.getClassLoader().getResource("");
//        System.out.println(url);
    }
}
