package com.pinyougou.search.service.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public void onMessage(Message message) {
        //监听到了消息就去更新索引库
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
            for (TbItem tbItem : itemList) {
                System.out.println("id:" + tbItem.getId() + " title:" + tbItem.getTitle());
                Map specMap = JSON.parseObject(tbItem.getSpec());
                tbItem.setSpecMap(specMap);
            }
            itemSearchService.importList(itemList);//导入索引库
            System.out.println("成功导入索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
