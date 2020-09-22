package com.zeyigou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zeyigou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("itemSearch")
public class ItemSearchController {
    @Autowired
    private ItemSearchService itemSearchService;
    @RequestMapping("search")
    public Map search(@RequestBody Map paramMap){
        //System.out.println(paramMap.toString());
        Map m = itemSearchService.search(paramMap);
        System.out.println(m.toString());
        return m;
    }
}
