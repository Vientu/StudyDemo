package com.zeyigou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.vientu.util.HttpClient;
import com.zeyigou.pay.service.PayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * PayServiceImpl
 *
 * @Author Vientu
 * @Date 2020/10/8 17:20
 */
@Service
public class PayServiceImpl implements PayService {
    //第一部分：准备参数
    //1.1 下单请求地址
    private String orderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //1.2 查询订单的地址
    private String queryOrderUrl = "https://api.mch.weixin.qq.com/pay/orderquery";
    // 关闭订单的地址
    private String closeOrderUrl = "https://api.mch.weixin.qq.com/pay/closeorder";
    //1.3 公众号
    @Value("${appid}")
    private String appid;
    //1.4 商户号
    @Value("${partner}")
    private String partner;
    //1.5 商户密匙
    @Value("${partnerkey}")
    private String partnerkey;
    //1.6 通知地址
    @Value("${notifyurl}")
    private String notifyurl;

    //1.向微信后台发出下单请求
    //参数1：订单号；参数2：订单总金额
    @Override
    public Map createNative(String outTradeNo, String totalFee) {
        try {
            //第一阶段：准备要发送的数据
            //1.1 准备要发送的数据
            Map param = new HashMap();
            param.put("appid",appid);                               //公众号
            param.put("mch_id",partner);                            //商户号
            param.put("sign","zeyigou");                            //签名
            param.put("body","这书一件不错的好商品！");                 //描述
            param.put("out_trade_no",outTradeNo);                   //订单号
            param.put("total_fee",totalFee);                        //总金额
            param.put("notify_url",notifyurl);                      //通知地址
            param.put("trade_type","NATIVE");                       //交易类型
            param.put("spbill_create_ip","127.0.0.1");              //终端ip
            param.put("nonce_str", WXPayUtil.generateNonceStr());   //随机字符串

            //1.2 将map数据转换为xml数据
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);

            //第二阶段：开始向微信后台发出下单请求
            HttpClient httpClient = new HttpClient(orderUrl);
            httpClient.setHttps(true);                          //发送HTTP请求
            httpClient.setXmlParam(signedXml);                  //设置要发送的数据
            httpClient.post();                                  //发送数据

            //第三阶段：获取微信后台返回的结果
            //3.1 得到返回的xml数据
            String content = httpClient.getContent();           //xml数据
            //3.2 将xml数据转换为map
            Map<String, String> contentMap = WXPayUtil.xmlToMap(content);
            //3.3 定义返回的结果集map
            Map resultMap = new HashMap();
            resultMap.put("tradeNo",outTradeNo);
            resultMap.put("totalFee",totalFee);
            resultMap.put("code_url",contentMap.get("code_url"));

            //3.4 返回
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap();
    }

    //2.查询支付状态
    @Override
    public Map queryPayStatus(String tradeNo) {
        try {
            //第一阶段：准备要发送的数据
            //2.1 准备要发送的数据
            Map paramMap = new HashMap();
            paramMap.put("appid",appid);                               //公众号
            paramMap.put("mch_id",partner);                            //商户号
            paramMap.put("sign","zeyigou");                            //签名
            paramMap.put("out_trade_no",tradeNo);                      //订单号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());   //随机字符串
            //2.2 将map转换为xml数据
            String signedXml = WXPayUtil.generateSignedXml(paramMap,partnerkey);

            //第二阶段：开始发送数据
            HttpClient httpClient = new HttpClient(queryOrderUrl);
            httpClient.setHttps(true);                          //发送HTTP请求
            httpClient.setXmlParam(signedXml);                  //设置要发送的数据
            httpClient.post();                                  //发送数据

            //第三阶段：得到返回的数据
            String content = httpClient.getContent();
            //3.1 转换为map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            //3.2 返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }

    //3.关闭订单
    @Override
    public Map closePay(String tradeNo) {
        try {
            //第一阶段：准备要发送的数据
            //3.1 准备要发送的数据
            Map paramMap = new HashMap();
            paramMap.put("appid",appid);                               //公众号
            paramMap.put("mch_id",partner);                            //商户号
            paramMap.put("sign","zeyigou");                            //签名
            paramMap.put("out_trade_no",tradeNo);                      //订单号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());   //随机字符串
            //3.2 将map转换为xml数据
            String signedXml = WXPayUtil.generateSignedXml(paramMap,partnerkey);

            //第二阶段：开始发送数据
            HttpClient httpClient = new HttpClient(closeOrderUrl);
            httpClient.setHttps(true);                          //发送HTTP请求
            httpClient.setXmlParam(signedXml);                  //设置要发送的数据
            httpClient.post();                                  //发送数据

            //第三阶段：得到返回的数据
            String content = httpClient.getContent();
            //3.1 转换为map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            //3.2 返回
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
