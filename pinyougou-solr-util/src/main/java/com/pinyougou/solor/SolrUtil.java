package com.pinyougou.solor;

import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class SolrUtil {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 从数据库导入数据到solr库中
     */
    public void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//status为1表示以审核的商品
        List<TbItem> tbItemlist = itemMapper.selectByExample(example);
        //数据库查询得到商品列表
        System.out.println("=======商品列表======");
        for (TbItem tbItem : tbItemlist) {
            //将spec字段(json字符串)转换为map
            Map specMap = JSONObject.parseObject(tbItem.getSpec());
            tbItem.setSpecMap(specMap);//给这个动态的spec_*赋值
            System.out.println(tbItem.getTitle());//看循环出来了什么
        }
        solrTemplate.saveBeans(tbItemlist);
        solrTemplate.commit();
        System.out.println("create indices done");
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        context.getBean(SolrUtil.class).importItemData();
    }
}
