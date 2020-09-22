package com.vientu.util;

import com.alibaba.fastjson.JSON;
import com.zeyigou.mapper.TbItemMapper;
import com.zeyigou.pojo.TbItem;
import com.zeyigou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    //1.将tb_item表中的数据导入索引库中
    public void importData(){
        //1.1 导入sku商品列表
        //1.1.1 定义查询条件（已审核的商品）并查询
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        //1.1.2 处理动态域字段
        for (TbItem tbItem : tbItems) {
            //①到规格字段
            String spec = tbItem.getSpec();
            //②将规格字段转换为map
            Map map = JSON.parseObject(spec,Map.class);
            //③将specMap与item进行绑定
            tbItem.setSpecMap(map);
        }
        //1.2 保存到索引库中
        solrTemplate.saveBeans(tbItems);
        //1.3 提交改变
        solrTemplate.commit();
    }
}
