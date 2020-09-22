package com.zeyigou.portal.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.zeyigou.content.service.ContentService;
import com.zeyigou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @Reference
    private ContentService contentService;

    //1.根据分类id查询广告列表
    @RequestMapping("index")
    public List<TbContent> findCategory(Long cid){
        return contentService.findCategory(cid);
    }
}
