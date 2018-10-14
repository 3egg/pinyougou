package com.pinyougou.page.listener;

import com.pinyougou.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class GenHtmlListener implements MessageListener {

    @Autowired
    private PageService pageService;

    //监听生产者的消息
    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage){
            //接收消息
            try {
                ObjectMessage message1 = (ObjectMessage) message;
                Long[] ids = (Long[]) message1.getObject();
                for (Long id : ids) {
                    pageService.getHtml(id);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
