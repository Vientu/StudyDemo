package com.zeyigou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zeyigou.pojo.TbItem;
import com.zeyigou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    //1.根据请求参数进行查询
    @Override
    public Map search(Map paramMap) {
        //1. 定义结果map
        Map resultMap = new HashMap();
        System.out.println(paramMap.toString());
        //1.1 定义查询对象
        Query query = new SimpleQuery("*:*");
        //1.2 添加查询条件
        if (paramMap!=null && StringUtils.isNotBlank(paramMap.get("keywords")+"")){
            //1.2.1 如果存在关键字就添加查询条件
            Criteria criteria = new Criteria("item_keywords").is(paramMap.get("keywords"));
            //1.2.2 将查询条件和查询对象绑定
            query.addCriteria(criteria);
        }else {
            return null;
        }
        //1.3 进行关键字查询
        ScoredPage<TbItem> itemScoredPage = solrTemplate.queryForPage(query, TbItem.class);
        //1.4 得到查询结果
        List<TbItem> content = itemScoredPage.getContent();
        //1.5 将结果放到结果map中
        resultMap.put("rows",content);
        //1.6 返回结果map
        return resultMap;
    }
}
