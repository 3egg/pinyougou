package com.pinyougou.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
	@Autowired
    private RedisTemplate redisTemplate;


	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		//调用缓存,把数据存入缓存
        saveToRedis();
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
		saveToRedis();
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<Map> findSpecList(Long id) {
	    //这是是找到type_template里面的specId . 然后把这些数据交给页面
        //要到所有的规格明细的list
        //先通过id找到对应的typeTemplate , 这个里面有个列名叫做 spec_ids
        TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        //这里的map数据为[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        List<Map> list = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
        for (Map map : list) {
            //循环得到里面的spec_Ids数据
            //然后查询spec_id得到对应的值
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            //[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            //map.get("id")就可以获取到对应的27
            criteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
            //执行的sql select * from tb_specification_option where spec_id = 27;
            List<TbSpecificationOption> tbSpecificationOptionList = specificationOptionMapper.selectByExample(example);
            //然后往map里面put数据格式变为
            // [{"id":27,"text":"网络",options:[{"id":122,"optionName":"40英寸","orders":1,"specId":33},{...}]}]
            map.put("options", tbSpecificationOptionList);
        }
        return list;
    }

    /**
     * 将数据存入缓存
     */
    private void saveToRedis(){
        List<TbTypeTemplate> tbTypeTemplateList = findAll();
        for (TbTypeTemplate tbTypeTemplate : tbTypeTemplateList) {
            List<Map> brandList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
            //hash大key为brandList , field为模块id,存的值value为品牌的名称
            //[{"id":1,"text":"联想"},{"id":3,"text":"三星"},{...}]
            redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),brandList);

            //根据模块id查询到对应的规格列表,大kye为specList,field为模块id,value为specList
            //[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            //需要这样的数据[{"id":27,"text":"网络",options:[{"id":122,"optionName":"40英寸","orders":1,"specId":33},{...}]}]
            List<Map> specList = findSpecList(tbTypeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(), specList);
        }
    }
}









