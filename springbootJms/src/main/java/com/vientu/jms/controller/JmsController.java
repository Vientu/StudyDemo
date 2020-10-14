package com.vientu.jms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JmsController
 *
 * @Author Vientu
 * @Date 2020/9/28 20:47
 */
@RestController
@RequestMapping("jms")
public class JmsController {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    //1.发送消息
    @GetMapping("send")
    public void sendMessage(String message){
        //1.1)直接发送消息
        jmsMessagingTemplate.convertAndSend("vientu",message);
    }
}
