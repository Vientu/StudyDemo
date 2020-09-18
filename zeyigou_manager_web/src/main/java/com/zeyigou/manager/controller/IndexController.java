package com.zeyigou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class IndexController {

    //1.得到管理员名
    @RequestMapping("/getName")
    public Map<String ,String> getName(){
        //1.得到登录名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("name=>"+name);
        //2.定义返回Map
        Map<String,String> map = new HashMap<>();
        map.put("name",name);
        //3. 返回
        return map;
    }
}
