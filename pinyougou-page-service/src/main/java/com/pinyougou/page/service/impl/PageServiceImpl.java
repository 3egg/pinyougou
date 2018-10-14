package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.service.PageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Autowired
    private FreeMarkerConfigurer configurer;

    @Override
    public boolean getHtml(Long goodsid) {
        //通过goodsid获取spu的数据
        TbGoodsExample example = new TbGoodsExample();
        TbGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(goodsid);
        criteria.andIsDeleteEqualTo(false);
        criteria.andAuditStatusEqualTo("1");
        List<TbGoods> tbGoods = goodsMapper.selectByExample(example);
        if (tbGoods != null && tbGoods.size() > 0) {
            TbGoodsDesc goodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsid);

        }
        return false;
    }

    private void genFreemarkerhtml(TbGoods goods,TbGoodsDesc desc){
        FileWriter writer = null;
        try {
            //模块 + 数据集 = html
            Configuration configuration = configurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            //数据集
            Map<String,Object> model = new HashMap<>();
            model.put("goods", goods);
            model.put("goodsDesc", desc);
            writer = new FileWriter(new File("D:\\freemarker\\"+goods.getId()+".html"));
            template.process(model, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
