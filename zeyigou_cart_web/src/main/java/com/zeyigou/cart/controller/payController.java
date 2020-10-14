package com.zeyigou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zeyigou.order.service.OrderService;
import com.zeyigou.pay.service.PayService;
import com.zeyigou.pojo.Result;
import com.zeyigou.pojo.TbOrder;
import com.zeyigou.pojo.TbPayLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * payController
 *
 * @Author Vientu
 * @Date 2020/10/8 16:50
 */
@RestController
@RequestMapping("pay")
public class payController {
    @Reference
    private PayService payService;
    @Reference
    private OrderService orderService;

    //1.向微信后台发出下单请求
    @RequestMapping("createNative")
    public Map createNative(){
        //1.1 得到当前登录的用户id
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //1.2 从redis中得到支付日志
        TbPayLog payLog = orderService.getPayLogFromRedis(name);
        //1.3 向微信后台发出下单请求（参数2：订单总金额，以分为单位）
        return payService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
    }

    //2.查询订单支付状态
    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String tradeNo){
        //2.1 定义循环变量
        int x = 0;
        Result result = null;
        while (true){
            Map map = payService.queryPayStatus(tradeNo);
            if (map == null){
                result = new Result(false,"支付失败！");
            }
            if ("SUCCESS".equals(map.get("trade_state").toString())){
                result = new Result(true,"支付成功！");
                //修改支付日志及订单的相关信息
                orderService.updatePayLogAndOrder(tradeNo,map.get("transcation_id")+"");
                break;
            }
            x++;
            if (x>=100){
                result = new Result(false,"二维码超时");
                break;
            }
            //每隔3s查询一次
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
