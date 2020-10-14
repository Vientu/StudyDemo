package com.vientu.listener;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * MessageListenerQueue
 *
 * @Author Vientu
 * @Date 2020/9/25 19:22
 */
@Component
public class MessageListenerQueue implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            //1.转换信息为textMassage消息
            TextMessage msg = (TextMessage) message;
            //2.得到消息内容
            String text = msg.getText();
            //3.打印
            System.out.println("text = "+text);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
