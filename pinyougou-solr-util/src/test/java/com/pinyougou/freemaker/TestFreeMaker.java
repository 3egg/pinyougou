package com.pinyougou.freemaker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class TestFreeMaker {

    public static void main(String[] args) throws IOException, TemplateException {
        //1.创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板所在所在的目录
        configuration.setDirectoryForTemplateLoading(new File("C:/Users/lisandan/IDEA_DEV/ideaproject/pinyougou-parent/pinyougou-solr-util/src/main/resources"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate("test.ftl");
        //List指令
        List<Object> goodsList = new ArrayList<>();
        Map<String,Object> goods1 = new HashMap<>();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        Map<String,Object> goods3 = new HashMap<>();
        goods3.put("name", "香蕉");
        goods3.put("price", 6.6);
        Map<String,Object> goods4 = new HashMap<>();
        goods4.put("name", "橘子");
        goods4.put("price", 8.8);
        goodsList.add(goods1);
        goodsList.add(goods3);
        goodsList.add(goods4);
        //创建数据模型
        Map<String ,Object > map = new HashMap<>();
        map.put("name", "张三");
        map.put("message", "欢迎welcome");
        map.put("success", true);
        map.put("goodsList", goodsList);
        map.put("today", new Date());
        map.put("point", 1024102424);
        //创建writer对象
        Writer out = new FileWriter(new File("d:\\test.html"));
        //输出
        template.process(map, out);
        out.close();
    }
}
