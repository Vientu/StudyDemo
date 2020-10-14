package com.zeyigou.search.service.listener;

import com.zeyigou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * MessageListenerDelete
 *
 * @Author Vientu
 * @Date 2020/9/25 20:59
 */
@Component
public class MessageListenerDelete implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            //1.获得发送过来的数据并转成Long数组
            ObjectMessage object = (ObjectMessage) message;
            Long[] ids = (Long[]) object.getObject();
            //2.从索引库中删除商品
            itemSearchService.deleteIndexByGoodsId(ids);
            //3.打印
            System.out.println("从索引库中删除商品.");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
