package com.zeyigou.order.service;

import com.zeyigou.pojo.TbOrder;
import com.zeyigou.pojo.TbPayLog;

public interface OrderService {
    void add(TbOrder order);

    TbPayLog getPayLogFromRedis(String name);

    void updatePayLogAndOrder(String tradeNo, String transcation_id);
}
