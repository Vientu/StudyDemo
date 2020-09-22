package com.vientu.test;

import com.zeyigou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class TestSolrTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    //删除全部索引
    public void test07(){
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //6. 分页查询
    @Test
    public void test06(){
        //6.1)定义分页查询对象
        SimpleQuery query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is("手机");
        //1.2.2 将查询条件和查询对象绑定
        query.addCriteria(criteria);
        //6.2)设置分页查询参数
        query.setOffset(5);     //偏移量（第几条开始）--->（page-1）* pagesize
        query.setRows(10);      //每页记录大小---->pagesize

        //6.3)开始分页查询
        ScoredPage<TbItem> itemScoredPage = solrTemplate.queryForPage(query, TbItem.class);
        //6.4)得到查询结果
        int totalPages = itemScoredPage.getTotalPages();    //总页数
        long total = itemScoredPage.getTotalElements();     //总记录数
        List<TbItem> content = itemScoredPage.getContent(); //每页的记录集合
        //6.5)遍历分页内容
        content.forEach(System.out::println);
    }

}
