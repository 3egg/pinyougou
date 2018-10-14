package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //商品为未审核
        goods.getGoods().setAuditStatus("0");
        goods.getGoods().setIsDelete(false);
        //先插入goods
        goodsMapper.insert(goods.getGoods());
        //根据 select last_insert_id as id 得到上次插入数据库的id , 插入对应的goodsDesc
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置goodDesc的goodsId
        tbGoodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展数据
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            //循环这个itemList,
            List<TbItem> itemList = goods.getItemList();
            for (TbItem tbItem : itemList) {
                //tbItem的列很多... title ,sellPoint,price,barcode,image,categoryId,status,
                //createTime,itemSn,costPrice,marketPrice,isDefault,
                //goodsId,sellerId,cartThumbnail,category,brand,spec,seller
                String title = goods.getGoods().getGoodsName();
                //spec表示[{"机身内存":"16G","网络":"联通3G"},{}]
                Map<String, Object> specMap = JSON.parseObject(tbItem.getSpec());
                for (String key : specMap.keySet()) {
                    title += "" + specMap.get(key);
                }
                tbItem.setTitle(title);
                setItemValues(goods, tbItem);
                itemMapper.insert(tbItem);
            }
        } else {
            //没有启用规格 isSpecEnable=0
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
            item.setPrice(goods.getGoods().getPrice());//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            setItemValues(goods, item);
            itemMapper.insert(item);

        }
       /* //循环这个itemList,
        List<TbItem> itemList = goods.getItemList();
        for (TbItem tbItem : itemList) {
            //tbItem的列很多... title ,sellPoint,price,barcode,image,categoryId,status,
            //createTime,itemSn,costPrice,marketPrice,isDefault,
            //goodsId,sellerId,cartThumbnail,category,brand,spec,seller
            String title = goods.getGoods().getGoodsName();
            //spec表示[{"机身内存":"16G","网络":"联通3G"},{}]
            Map<String, Object> specMap = JSON.parseObject(tbItem.getSpec());
            for (String key : specMap.keySet()) {
                title += "" + specMap.get(key);
            }
            tbItem.setTitle(title);//标题
            tbItem.setGoodsId(goods.getGoods().getId());//商品id
            tbItem.setSellerId(goods.getGoods().getSellerId());//卖家id
            tbItem.setCategoryid(goods.getGoods().getCategory3Id());//商品的最低分类
            tbItem.setCreateTime(new Date());//创建日期
            tbItem.setUpdateTime(new Date());//修改日期
            TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
            tbItem.setBrand(brand.getName());//品牌名称
            //分类名称
            TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
            tbItem.setCategory(itemCat.getName());//分类名称
            //商家名称
            TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
            tbItem.setSeller(seller.getNickName());//店铺名称
            //存储图片的地址
            List<Map> imgList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
            if(imgList.size() > 0){
                tbItem.setImage((String) imgList.get(0).get("url"));
            }
            itemMapper.insert(tbItem);
        }*/
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        goods.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(goods.getGoods());//商品tb_goods表
        tbGoodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());//商品明细good_desc表
        //删除原来的sku规格明细
        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(goods.getGoods().getId());
        // delete from tb_item where goods_id = ?
        itemMapper.deleteByExample(example);
        saveItemList(goods);
        //goodsMapper.updateByPrimaryKey(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        //商品的信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        //商品明细的信息
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        //sku规格的信息
        TbItemExample example = new TbItemExample();
        //拼接条件 select * from tb_item where goods_id = ?
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItemList = itemMapper.selectByExample(example);
        goods.setItemList(tbItemList);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //goodsMapper.deleteByPrimaryKey(id);
            //逻辑删除
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete(true);
            // update tb_goods set is_delete = 1 where id = ?
            goodsMapper.updateByPrimaryKey(goods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo(false);
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusEqualTo( goods.getAuditStatus());
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            /*这里的getIsDelete是返回boolen类型*/
			/*if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}*/

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        //根据页面传过来对应的status : 1,2,3修改数据库对应的值
        // update tb_goods set status = status where id in (1,2,3)
        TbGoodsExample example = new TbGoodsExample();
        // where id in (1,2,3)
        example.createCriteria().andIdIn(Arrays.asList(ids));
        TbGoods goods = new TbGoods();
        // set tb_goods.status = status
        goods.setAuditStatus(status);
        goodsMapper.updateByExampleSelective(goods, example);
        /*for (Long id : ids) { 第二张方案循环一条条的改
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }*/
    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        criteria.andStatusEqualTo(status);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        return tbItems;
    }


    private void setItemValues(Goods goods, TbItem item) {
        item.setGoodsId(goods.getGoods().getId());//商品SPU编号
        item.setSellerId(goods.getGoods().getSellerId());//商家编号
        item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//修改日期

        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());

        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());

        //图片地址（取spu的第一个图片）
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList.size() > 0) {
            item.setImage((String) imageList.get(0).get("url"));
        }
    }

    /**
     * 插入SKU列表数据
     *
     * @param goods
     */
    private void saveItemList(Goods goods) {

        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem item : goods.getItemList()) {
                //构建标题  SPU名称+ 规格选项值
                String title = goods.getGoods().getGoodsName();//SPU名称
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                item.setTitle(title);

                setItemValues(goods, item);

                itemMapper.insert(item);
            }
        } else {//没有启用规格

            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//标题
            item.setPrice(goods.getGoods().getPrice());//价格
            item.setNum(99999);//库存数量
            item.setStatus("1");//状态
            item.setIsDefault("1");//默认
            item.setSpec("{}");//规格

            setItemValues(goods, item);

            itemMapper.insert(item);
        }

    }


}
