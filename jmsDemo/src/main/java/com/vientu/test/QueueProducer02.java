package com.vientu.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;

/**
 * QueueProducer01
 *
 * @Author Vientu
 * @Date 2020/9/25 18:11
 * [发布/订阅]消息发送方
 */
public class QueueProducer02 {
    public static void main(String[] args) {
        try {
            //1.得到工厂对象
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.129:61616");
            //2.得到连接对象
            Connection connection = connectionFactory.createConnection();
            //3.启动连接
            connection.start();
            //4.得到会话对象
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //5.创建消费者对象
            //5.1 创建目标对象
            Topic dest = new ActiveMQTopic("test-topic");
            //5.2 创建生产者对象
            MessageProducer producer = session.createProducer(dest);
            //5.3 定义发送消息的消息对象
            Message message = session.createTextMessage("你好，正在使用sub/pub发送消息！");
            //5.4 发送消息
            producer.send(dest,message);
            //5.5 关闭资源
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
