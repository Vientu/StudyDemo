package com.zeyigou.users.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.vientu.util.MD5Util;
import com.zeyigou.mapper.TbUserMapper;
import com.zeyigou.pojo.TbUser;
import com.zeyigou.users.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UserServiceImpl
 *
 * @Author Vientu
 * @Date 2020/9/29 16:15
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Queue jms;
    @Autowired
    private TbUserMapper userMapper;
    //1.获取验证码
    @Override
    public void getValidCode(String phone) {
        //1.1 生成六位数的验证码
        String code = (long)(Math.random()*1000000)+"";
        System.out.println("code ="+code);
        //1.2 将上面的验证码放到redis中
        redisTemplate.boundHashOps("validCode").put(phone,code);

        //1.3 向springbootJms这个微服务后台发送消息
        jmsTemplate.send(jms, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //1.3.1 创建一个map类似的消息
                MapMessage mapMessage = session.createMapMessage();
                //1.3.2 向消息中放内容
                mapMessage.setString("phone",phone);            //手机号
                mapMessage.setString("singName","优品购");     //模板签名
                mapMessage.setString("templateCode","SMS_203725961");   //模板code

                //1.3.3 处理模板参数
                Map<String ,String > paramMap = new HashMap<>();
                paramMap.put("code",code);
                //转换为字符串
                String templateParam = JSON.toJSONString(paramMap);
                //1.3.4 将templateParam放到消息中
                mapMessage.setString("templateParam",templateParam);
                //1.3.5 返回
                return mapMessage;
            }
        });
    }

    //2.验证验证码
    @Override
    public boolean isValide(String phone, String validCode) {
        //2.1 从redis中获取code
        String code = (String) redisTemplate.boundHashOps("validCode").get(phone);
        //2.2 比较是否相等
        return StringUtils.isNotBlank(code)&&code.equals(validCode);
    }

    //3.添加用户
    @Override
    public void add(TbUser user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //加密密码
        user.setPassword(MD5Util.MD5Encode(user.getPassword(),"utf-8"));
        //添加到数据库中
        userMapper.insert(user);

    }
}
