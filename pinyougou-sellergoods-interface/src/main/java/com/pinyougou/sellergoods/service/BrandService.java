package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

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




}
