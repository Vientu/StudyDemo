package com.vientu.jms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.*;

/**
 * SendQueueMessage
 *
 * @Author Vientu
 * @Date 2020/9/25 19:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*.xml")
public class SendMessage {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Queue springQueue;
    @Autowired
    private Topic springTopic;
    //1.发送点对点消息
    @Test
    public void testSendQueueMessage(){
        jmsTemplate.send(springQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("正在使用spring发送PTP消息----");
            }
        });
    }
    //2.发送发布/订阅消息
    @Test
    public void testTopicMessage(){
        jmsTemplate.send(springTopic, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("正在使用spring发送sub/pub消息-----");
            }
        });
    }
}
