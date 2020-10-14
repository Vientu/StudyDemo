package com.zeyigou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.zeyigou.pojo.TbItem;
import com.zeyigou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;


    //1.根据请求参数进行查询
    @Override
    public Map search(Map paramMap) {
        //1.1 定义结果map
        Map resultMap = new HashMap();
        System.out.println(paramMap.toString());
        //1.2 进行高亮查询
        Map hightLightMap = highlightQuery(paramMap);
        //1.2.1 将高亮查询结果放到大Map中
        resultMap.putAll(hightLightMap);

        //1.3 进行分组查询
        List<String> categoryList = categoryList(paramMap);
        //1.3.1 将分组查询结果放到大Map中
        resultMap.put("categoryList",categoryList);

        //1.4 根据分类名称得到模板id，从而在Redis中根据模板id查询到品牌列表和规格列表
        String category = "";
        //1.4.1 判断后台是否传入了分类值
        if (paramMap!=null){
            String str = paramMap.get("category")+"";
            if (StringUtils.isNotBlank(str)){
                category = paramMap.get("category")+"";
            }else if (categoryList!=null && categoryList.size()>0){
                category = categoryList.get(0);
            }
        }
        //1.4.2 从redis中根据分类名称找到模板id，从而找到品牌列表和规格列表
        Map brandSpecList = brandSpecList(category);
        //1.4.3 将分组查询结果放到大Map中
        resultMap.putAll(brandSpecList);

        //1.6 返回结果map
        return resultMap;
    }

    //2.进行高亮查询
    private Map highlightQuery(Map paramMap) {
        Map highligthMap = new HashMap();
        //第一部分：关键字查询
        //1.1 定义高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //1.2 添加关键字查询
        Criteria criteria = new Criteria("item_keywords");
        //1.3 根据传入的值构造查询条件
        if (paramMap!=null){
            if (StringUtils.isNotBlank(paramMap.get("keywords")+"")){
                //1.3.1 添加查询条件
                criteria.is(paramMap.get("keywords"));
            }
            //1.3.2 将查询条件与查询对象绑定
            query.addCriteria(criteria);

            //第二部分：过滤查询
            //2.1 进行分类查询
            if (StringUtils.isNotBlank(paramMap.get("category")+"")){
                //2.1.1 定义分类的过滤查询
                FilterQuery filterQuery =
                        new SimpleFilterQuery(new Criteria("item_category").is(paramMap.get("category")));
                //2.1.2 与查询你对象绑定
                query.addFilterQuery(filterQuery);
            }
            //2.2 进行品牌查询
            if (StringUtils.isNotBlank(paramMap.get("brand")+"")){
                //2.2.1 定义品牌的过滤条件
                FilterQuery filterQuery =
                        new SimpleFilterQuery(new Criteria("item_brand").is(paramMap.get("brand")));
                //2.2.2 与查询对象绑定
                query.addFilterQuery(filterQuery);
            }
            //2.3 进行规格品牌查询
            String specStr = paramMap.get("spec")+"";
            if (StringUtils.isNotBlank(specStr)){
                //2.3.1 将规格字符串装换为map对象
                Map specMap = JSON.parseObject(specStr,Map.class);
                //2.3.2 遍历map对象
                for (Object key : specMap.keySet()) {
                    //2.3.3 定义规格过滤对象
                    FilterQuery specFilterQuery =
                            new SimpleFilterQuery(new Criteria("item_spec_"+key).is(specMap.get(key)));
                    //2.3.4 与查询对象绑定
                    query.addFilterQuery(specFilterQuery);
                }
            }

            //第三部分：高亮页查询
            //3.1 定义设置参数的参数对象
            HighlightOptions options = new HighlightOptions();
            //3.2 向查询参数对象中添加参数
            options.addField("item_title");
            options.setSimplePrefix("<span style='color:red'>");
            options.setSimplePostfix("</span>");
            //3.3 将查询参数与查询对象绑定
            query.setHighlightOptions(options);
            //3.4 得到高亮页对象
            HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
            //3.5 得到高亮入口对象
            List<HighlightEntry<TbItem>> highlighted = itemHighlightPage.getHighlighted();
            //System.out.println("hightlighted-->"+highlighted);
            //3.6 遍历入口对象
            for (HighlightEntry<TbItem> highlightEntry : highlighted) {
                //3.6.1 得到原始未经过高亮的对象
                TbItem entity = highlightEntry.getEntity();
                //System.out.println("entity-->"+entity.getTitle());
                //3.6.2 得到高亮内容，因为所有的高亮对象放入到List集合中，是按顺序放入的，所以使用List集合
                List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
                //3.6.3 因为只放了一个字段，我们只取第一个值
                if (highlights!=null && highlights.size()>0){
                    //3.6.4 得到第一个高亮字段的值
                    HighlightEntry.Highlight highlight = highlights.get(0);
                    //3.6.5 因为高亮字段可能是多域，所以存放在list集合中
                    List<String> snipplets = highlight.getSnipplets();
                    //3.6.6 取第一个值
                    if (snipplets!=null && snipplets.size()>0){
                        //3.6.7 取得放入title高亮字段
                        String s = snipplets.get(0);
                        //3.6.8 将原来的实体中的标题字段改为高亮字段
                        entity.setTitle(s);
                    }
                }
                //System.out.println("entity-->"+entity.getTitle());
            }
            //3.6.9 将高亮对象放到集合中
            highligthMap.put("rows",itemHighlightPage.getContent());
        }
        //4.6.10 返回Map
        return highligthMap;
    }

    //3.进行分组查询
    private List<String> categoryList(Map paramMap) {
        //3.0 定义存放分组结果的list集合
        List<String> categoryList = new ArrayList<>();
        //3.1 定义分组查询条件对象
        SimpleQuery query = new SimpleQuery();
        //3.2 处理查询关键字
        if (paramMap!=null){
            //3.3 定义参数对象
            Criteria criteria = new Criteria("item_keywords");
            //3.4 添加参数
            if (StringUtils.isNotBlank(paramMap.get("keywords")+"")){
                criteria.is(paramMap.get("keywords"));
            }
            //3.5 与查询对象绑定
            query.addCriteria(criteria);
            //3.6 定义分组查询选项参数对象
            GroupOptions groupOptions = new GroupOptions();
            groupOptions.addGroupByField("item_category");
            //3.7 与查询对象绑定
            query.setGroupOptions(groupOptions);
            //3.8 得到分组页对象
            GroupPage<TbItem> itemGroupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
            //3.9 到分组结果对象
            GroupResult<TbItem> groupResult = itemGroupPage.getGroupResult("item_category");
            //3.10 得到分组条目对象
            Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
            //3.11 遍历条目对象
            for (GroupEntry<TbItem> groupEntry : groupEntries) {
                //3.12 得到分组的值
                String groupValue = groupEntry.getGroupValue();
                //3.13 添加到分组集合
                categoryList.add(groupValue);
            }

        }
        //3.14 返回分组集合
        return categoryList;
    }

    //4. 根据分类名称，找到模板id，从而找到品牌列表和规格列表
    private Map brandSpecList(String category) {
        //4.1 从redis中根据分类名称查到模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //4.2 根据模板id找出规格及品牌列表
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        //4.3 定义存放规格规格及品牌的Map
        Map specBrandMap = new HashMap();
        //4.4 将规格及品牌放到上面的map中
        specBrandMap.put("specList",specList);
        specBrandMap.put("brandList",brandList);
        //4.5 返回
        return specBrandMap;
    }

    //导入到索引库
    @Override
    public void importToIndex(List<TbItem> items) {
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }

    //从索引库从删除商品
    @Override
    public void deleteIndexByGoodsId(Long[] ids) {
        for (Long id : ids) {
            Query query = new SimpleQuery();
            query.addCriteria(new Criteria("item_goodsId").is(id));
            solrTemplate.delete(query);
            solrTemplate.commit();
        }
        System.out.println("删除索引成功！");
    }
}
