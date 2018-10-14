package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        //1.调用私有方法查询列表,高亮显示
        Map searchListMap = searchList(searchMap);
        //2.关键词分组查询category
        List<String> categoryList = searchCategoryList(searchMap);
        if (categoryList.size() > 0) {
            Map searchBrandAndSpecList = searchBrandAndSpecList(categoryList.get(0));
            map.putAll(searchBrandAndSpecList);
        }

        //3.查询品牌和规格列表
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)) {
            map.putAll(searchBrandAndSpecList(categoryName));
        } else {//没有分类的名称就按照第一个查询
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }
        map.put("categoryList", categoryList);
        map.putAll(searchListMap);
        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("itemSearchServiceImpl里面的deleteByGoodsIdS方法");
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //私有方法分组查询显示列表的分类信息
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        //关键字过滤查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组条件
        GroupOptions options = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(options);//分组条件为item_category搜索商品对应的分类
        GroupPage<TbItem> tbItemGroupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //通过solrTemplate得到了分组后的结果集
        GroupResult<TbItem> groupResult = tbItemGroupPage.getGroupResult("item_category");
        //分组后的结果集入口/即分页结果入口
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //分组后的内容入口
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            //将分组后的结果名称封装到返回值中,即得到了item_category对应的电视/手机内容
            list.add(tbItemGroupEntry.getGroupValue());
        }
        return list;
    }

    //私有方法(高亮),显示(搜索列表的关键字keywords)
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        //1.需要高亮的域
        HighlightOptions options = new HighlightOptions().addField("item_title");
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");//设置前后缀
        query.setHighlightOptions(options);//把需要高亮的选项交给query对象

        //2.关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);//query对象设置查询索引库的条件

        //3.按分类筛选
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //4.品牌筛选,品牌只有一个
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            //最后simpleQuery添加过滤的查询条件
            query.addFilterQuery(filterQuery);
        }
        //5.过滤规格,规格选项是动态变化的
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //List<TbItem> city = JSON.parseArray("city", TbItem.class);
        /*List<Map> list = new ArrayList<>();
        for (Map map1 : list) {
            //{k:v}
            Object name = map.get("name");
        }*/


        //6.按价格筛选
        if (!"".equals(searchMap.get("price")) && searchMap.get("price") != null) {
            //item_price : [0 TO 20] 筛选条件
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria priceCriteria = new Criteria("item_price");
            String price = (String) searchMap.get("price");
            //把-排除
            String[] split = price.split("-");
            //把*号排除
            if (!split[1].equals("*")) {
                priceCriteria.between(split[0], split[1], true, true);
            } else {
                priceCriteria.greaterThanEqual(split[0]);
            }
            filterQuery.addCriteria(priceCriteria);
            query.addFilterQuery(filterQuery);
        }

        //7.分页查询
        //提取页码
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;//默认第一页
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 20;//默认pageSize为20
        }
        query.setOffset((pageNo - 1) * pageSize);//从第几条记录查询
        query.setRows(pageSize);

        //8.排序
        String sortValue = (String) searchMap.get("sort");//asc desc
        String sortField = (String) searchMap.get("sortField");//排序字段
        if (sortValue != null && sortValue.equals("")) {
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }

        //具有高亮信息的tbItemList但是高亮选项的field为item_title,在list集合里面,需要多次循环才能得到
        HighlightPage<TbItem> tbItemList = solrTemplate.queryForHighlightPage(query, TbItem.class);
        for (HighlightEntry<TbItem> highlightEntry : tbItemList.getHighlighted()) {
            // List<HighlightEntry<TbItem>> highlighted = tbItemList.getHighlighted() 获取到了高亮集合的入口
            TbItem item = highlightEntry.getEntity();//获取到了单独的实体
            //System.out.println(item);
            if (highlightEntry.getHighlights().size() > 0 && highlightEntry.getHighlights().get(0).getSnipplets().size() > 0) {
                //如果高亮的size大小大于0且snipplets(高亮片段)大于0
                List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
                //System.out.println(highlights);
                //System.out.println(highlightEntry.getHighlights().get(0).getSnipplets());
                //就把需要高亮的snipplets的值设置给title属性
                item.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", tbItemList.getContent());
        //总记录数和总页数
        map.put("totalPages", tbItemList.getTotalPages());
        map.put("total", tbItemList.getTotalElements());
        return map;
    }

    //从redis中获取品牌和规格列表
    private Map searchBrandAndSpecList(String categoryName) {
        Map map = new HashMap();
        //根分类名找到它对应的typeId
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
        if (typeId != null) {
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }

        //List list = new LinkedList();
        //new ConcurrentHashMap<>();
        return map;
    }
}











