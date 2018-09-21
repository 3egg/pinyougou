package com.pinyougou.sellergoods.service;

import entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    //查询所有
    List<TbBrand> findAll();

    //分页查询+条件查询
    PageResult findPage(int pageNum,int pageSize,TbBrand brand);

    //添加品牌
    void add(TbBrand brand);

    //修改品牌
    void update(TbBrand brand);

    //按id查询
    TbBrand findOne(Long id);

    //批量删除
    void delete(Long[] ids);

    //获取所有的品牌下拉框,因为sql写的是from TbBrand
    List<Map> selectOptionList();


}
