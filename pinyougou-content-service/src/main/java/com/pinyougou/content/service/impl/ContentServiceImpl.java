package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@SuppressWarnings("all")
@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbContent content) {


        contentMapper.insert(content);

        //清除缓存
        try {
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改
     * 修改前和修改后的  分类id 缓存都需要清除
     */
    @Override
    public void update(TbContent content) {
        //查询修改前的分类Id , 原来分类的id
        Long id = content.getId();//通过页面传过来得id
        TbContent tbContent = contentMapper.selectByPrimaryKey(id);//找到数据库得content对象
        Long categoryId = tbContent.getCategoryId();//通过这个对象找到它的categoryId
        //修改就要根据大key删除里面的field
        redisTemplate.boundHashOps("content").delete(categoryId);
        contentMapper.updateByPrimaryKey(content);
        //如果分类ID发生了修改,清除修改后的分类ID的缓存
        Long updateId = content.getCategoryId();//从页面传过来的分类id
        if (categoryId != updateId) {
            //如果从数据库传过来的id和页面传过来的id不一样就要把updateId这个field删除
            redisTemplate.boundHashOps("content").delete(updateId);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //清除缓存
            Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();//广告分类ID
            redisTemplate.boundHashOps("content").delete(categoryId);
            contentMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();

        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }

        }

        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<TbContent> findByCategoryId(Long categoryId) {

        //这里是获取大key , 这里使用hash存取数据  key : field1 --->value1
        //                                           field2 --->value2
        List<TbContent> contentList = (List<TbContent>)
                redisTemplate.boundHashOps("content").get(categoryId);
        //这里是使用了hash数据结构来存取数据 . 大key为content . 通过不同的field(categoryId) 来获取到不同的value
        if (contentList == null) {
            //根据广告分类ID查询广告列表
            TbContentExample contentExample = new TbContentExample();
            Criteria criteria2 = contentExample.createCriteria();
            criteria2.andCategoryIdEqualTo(categoryId);
            criteria2.andStatusEqualTo("1");//开启状态
            contentExample.setOrderByClause("sort_order");//排序
            //没有值就把List<Tbcontent>这个集合数据存进数据库
            contentList = contentMapper.selectByExample(contentExample);
            //这里的field为categoryId spring-data-redis把它序列化了,value是list集合对象数据,也给序列化了
            redisTemplate.boundHashOps("content").put(categoryId, contentList);
        } else {
            System.out.println("************从缓存读取数据****************");
        }
        return contentList;
    }

}
