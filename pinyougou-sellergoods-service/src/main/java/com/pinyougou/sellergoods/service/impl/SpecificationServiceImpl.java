package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		specificationMapper.insert(specification.getSpecification());
        for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
            //把规格选项的SpecId设置为规格的主键id
            specificationOption.setSpecId(specification.getSpecification().getId());
            //然后调用specificationOptionMapper循环插入specificationOption这个对象
            specificationOptionMapper.insert(specificationOption);
        }
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
        //通过specification获取到里面的tbspecification对象
		specificationMapper.updateByPrimaryKey(specification.getSpecification());

		//把原来的tbspecificationOption全部删除,然后再去添加??
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        //tbspecification的id就是tbspecificationOption里面的specid. 把条件设置进去
        criteria.andSpecIdEqualTo(specification.getSpecification().getId());

        //点击保存按钮,就把页面的数据全部获取到,然后删除数据库中的数据,最后把页面中数据保存进数据库中
        //把里面的tbspecificationOption删除
        specificationOptionMapper.deleteByExample(example);
        for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
            //循环添加tbSpecificationOption到数据库
            specificationOptionMapper.insert(tbSpecificationOption);
        }
    }

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
        //根据页面传过来的id找到了tbSpecification这个数据库表对应的对象
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //规格明细根据条件即specid找到对应的TbspecificationOption
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        //根据example找到对应的List<TbspecificationOption>集合
        List<TbSpecificationOption> tbSpecificationOptionList = specificationOptionMapper.selectByExample(example);
        //然后创建一个空的specification对象把从数据库查到的值set进去
        Specification specification = new Specification();
        //根据页面传过来的id找到了对应的    tbSpecification
        specification.setSpecification(tbSpecification);
        //根据tbSpecification的id找到对应的specid找到对应的tbSpecificationOption集合set进去
        specification.setSpecificationOptionList(tbSpecificationOptionList);
        return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
		    //删除规格也要删除对应的规格选项specificationOption
			specificationMapper.deleteByPrimaryKey(id);
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            //通过specification的id找到对应的specificationOption
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            //example封装了查询条件,然后通过specificationId找到了对应的tbSpecificationOptionList
            //然后通过这个example这个封装的条件删除数据
            specificationOptionMapper.deleteByExample(example);
        }
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();

    }

}
