package com.zeyigou.search.service;

import com.zeyigou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    Map search(Map paramMap);

    void importToIndex(List<TbItem> items);

    void deleteIndexByGoodsId(Long[] ids);
}
