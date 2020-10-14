package com.zeyigou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zeyigou.pay.service.PayService;
import com.zeyigou.pojo.Result;
import com.zeyigou.pojo.TbPayLog;
import com.zeyigou.pojo.TbSeckillOrder;
import com.zeyigou.seckill.service.SeckillOrderService;
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
    private SeckillOrderService orderService;

    //1.向微信后台发出下单请求
    @RequestMapping("createNative")
    public Map createNative(){
        //1.1 得到当前登录的用户id
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //1.2 从redis中得到秒杀订单
        TbSeckillOrder order = orderService.getOrderFromRedis(name);
        //1.3 向微信后台发出下单请求（参数2：订单总金额，以分为单位）
        return payService.createNative(order.getId()+"",order.getMoney().doubleValue()*100 + "");
    }

    //2.查询订单支付状态
    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String tradeNo){
        //2.0 得到当前登录的用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
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
                //从redis中得到订单并保存到db中，并从redis中删除此订单
                orderService.saveOrderToDB(tradeNo,name,map.get("transcation_id").toString());
                break;
            }
            x++;
            if (x>=20){
                result = new Result(false,"二维码超时");
                //发出关闭订单请求
                Map closeMap = payService.closePay(tradeNo);
                //对关闭的异常情况进行处理
                if ("SUCCESS".equals(closeMap.get("return_code")+"") && "FAIL".equals(closeMap.get("result_code")+"")){
                    if ("ORDERPAID".equals(closeMap.get("err_code")+"")){       //代表支付成功的情况
                        //从redis中得到订单信息并保存到db中，并从redis中删除此订单
                        orderService.saveOrderToDB(tradeNo,name,map.get("transcation_id").toString());
                        result = new Result(true,"支付成功！");
                    }
                }
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
