package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize,TbBrand brand) {
        PageHelper.startPage(pageNum,pageSize);
        //这些条件的设置是前端查询时添加的条件
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if(brand != null){//如果查询条件不为null
            if(StringUtils.isNotEmpty(brand.getName())){
                criteria.andNameLike(brand.getName());
            }
            if(StringUtils.isNotEmpty(brand.getFirstChar())){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        //如果没有传条件.example就为null,即查询所有
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
        //返回总计多少行数和getResult,List<E> 即rows
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(Long[] ids) {
       /* //或者delete from TbBrand where id in();
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        //拼条件
        criteria.andIdIn(Arrays.asList(ids));
        brandMapper.deleteByExample(example);*/
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        //因为数据库的数据为 [{"id":1,"text":"联想"},{"id":3,"text":"三星"}...]
        List<Map> optionList = new ArrayList<>();
        //得到了所有的tbBrands集合对象数据
        List<TbBrand> tbBrands = brandMapper.selectByExample(null);
        for (TbBrand tbBrand : tbBrands) {
            Map map = new HashMap();
            //把tbBrand数据放进map中
            map.put("id", tbBrand.getId());//List<Map>的格式
            //数据库的数据就是[{"id":1,"text":"联想"},{"id":3,"text":"三星"}...]
            map.put("text", tbBrand.getName());
            optionList.add(map);
        }
        //return optionList;
        //第二张方案
        //所以手写新增的sql select id , name as test from Tb_Brand ;
        //其中mybatis里面的resultMap为 java.util.map
        //然后把数据封装成List<Map>
        return brandMapper.selectOptionList();
    }


}
