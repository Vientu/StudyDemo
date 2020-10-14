package com.zeyigou.pay.service;

import java.util.Map;

public interface PayService {
    Map createNative(String outTradeNo, String s);

    Map queryPayStatus(String tradeNo);

    Map closePay(String tradeNo);
}
