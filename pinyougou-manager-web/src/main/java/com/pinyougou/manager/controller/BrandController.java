package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(int page,int rows){
        return brandService.findPage(page, rows,new TbBrand());
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){//接收json对象的数据
        //页码传过来的数据为{name:xx,firstChar:X}
        try {
            brandService.add(brand);
            return new Result(true,"add succeed");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"add failed");
        }
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        //返回TbBrand这个对象的json格式
        return brandService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"update succeed");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"update failed");
        }
    }

    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"delete succeed");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"delete failed");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,int page,int rows){
        return brandService.findPage(page, rows, brand);
    }


}
