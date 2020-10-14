package com.zeyigou.users.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.vientu.util.PhoneFormatCheckUtils;
import com.zeyigou.pojo.Result;
import com.zeyigou.pojo.TbUser;
import com.zeyigou.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * UserController
 *
 * @Author Vientu
 * @Date 2020/9/29 16:25
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Reference
    private UserService userService;

    //1.获取验证码
    @RequestMapping("getValidCode")
    public Result getValidCode(String phone){
        //1.1 判断手机号是否合法
        if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone)){
            userService.getValidCode(phone);
            return new Result(true,"发送验证码成功！");
        }else {
            return new Result(false,"手机号不合法！");
        }
    }

    //2.添加用户
    @RequestMapping("add")
    public Result add(@RequestBody TbUser user,String validCode){
        try {
            //2.1 进行验证码的比较
            if (userService.isValide(user.getPhone(),validCode)){
                userService.add(user);
                return new Result(true,"添加成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"添加失败！");
    }

    //3.获取登录名
    @RequestMapping("getName")
    public Map getName(){
        //3.1 定义要返回的map集合
        Map map = new HashMap();
        //3.2 获取当前登录名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //3.3 将登录名放到集合中
        map.put("name",name);
        //3.4 返回
        return map;
    }

}
