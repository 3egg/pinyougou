package com.pinyougou.search.service.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage)message;
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));
            //System.out.println("id:" + tbItem.getId() + " title:" + tbItem.getTitle());
            System.out.println("删除所有成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
