package com.vientu.jms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.vientu.jms.util.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * MyMessageListener
 *
 * @Author Vientu
 * @Date 2020/9/29 17:07
 */
@Component
public class MyMessageListener {
    @Autowired
    private SmsUtil smsUtil;
    @JmsListener(destination = "vientu")
    public void getMessage(String msg){
        System.out.println("[接收到消息]：" + msg);
    }

    //2.监听来自jms的消息
    @JmsListener(destination = "jms")
    public void getJmsMessage(Map<String ,String > map){
        try {
            System.out.println("获取到消息："+map);
            smsUtil.sendSms(map.get("phone"),
                    map.get("singName"),
                    map.get("templateCode"),
                    map.get("templateParam"));
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
