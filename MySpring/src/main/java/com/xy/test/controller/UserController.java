package com.xy.test.controller;

import com.xy.myspring.bean.annotation.AutoWried;
import com.xy.myspring.bean.annotation.Controller;
import com.xy.test.service.UserService;

/**
 * Created With IntelliJ IDEA
 * User: yao
 * Date:2020/12/03
 * Description:
 * Version:V1.0
 */
@Controller
public class UserController {
    @AutoWried
    private UserService userService;



    public void hello(){

        String hello = userService.hello();
        System.out.println("controller.....");
    }
}
