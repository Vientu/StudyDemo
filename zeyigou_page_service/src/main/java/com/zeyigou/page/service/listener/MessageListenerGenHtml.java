package com.zeyigou.page.service.listener;

import com.zeyigou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;

/**
 * MessageListenerGenHtml
 *
 * @Author Vientu
 * @Date 2020/9/25 20:48
 */
@Component
public class MessageListenerGenHtml implements MessageListener{

    @Autowired
    private PageService pageService;

    @Override
    public void onMessage(Message message) {
        try {
            //1.得到发来的消息，并转换为Long
            TextMessage msg = (TextMessage) message;
            Long id = new Long(msg.getText());
            //2.生成静态页面
            pageService.genHtml(id);
            //3.提示
            System.out.println("生成静态页面成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
