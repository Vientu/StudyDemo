package com.zeyigou.search.service.listener;

import com.alibaba.fastjson.JSON;
import com.zeyigou.pojo.TbItem;
import com.zeyigou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageListenerUpdateStatus
 *
 * @Author Vientu
 * @Date 2020/9/25 22:33
 */
@Component
public class MessageListenerUpdateStatus implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            //1.接受商品审核发来的消息
            TextMessage msg = (TextMessage) message;
            String text = msg.getText();
            //2.转换成List<TbItems>集合
            List<TbItem> items = JSON.parseArray(text, TbItem.class);
            //3.导入到索引库中
            itemSearchService.importToIndex(items);
            //4.打印提示
            System.out.println("导入索引库成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
