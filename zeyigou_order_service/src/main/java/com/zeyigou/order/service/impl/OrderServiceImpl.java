package com.zeyigou.order.service.impl;

import com.vientu.util.IdWorker;
import com.zeyigou.group.Cart;
import com.zeyigou.mapper.TbOrderItemMapper;
import com.zeyigou.mapper.TbOrderMapper;
import com.zeyigou.mapper.TbPayLogMapper;
import com.zeyigou.order.service.OrderService;
import com.zeyigou.pojo.TbOrder;
import com.zeyigou.pojo.TbOrderItem;
import com.zeyigou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * OrderServiceImpl
 *
 * @Author Vientu
 * @Date 2020/10/6 0:37
 */
public class OrderServiceImpl implements OrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbPayLogMapper payLogMapper;

    //1.保存订单
    @Override
    public void add(TbOrder order) {
        //1.1 得到购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        //定义订单i字符串d
        String ids = "";
        //定义总金额
        double totalMoney = 0;
        //1.2 遍历购物车列表
        for (Cart cart : cartList) {
            //1.2.1 构造要添加的订单对象
            TbOrder tbOrder = new TbOrder();
            //1.2.2 生成一个订单id
            long nextId = idWorker.nextId();
            //1.2.3 设置订单相关属性
            order.setOrderId(nextId);
            order.setUserId(tbOrder.getUserId());
            order.setSourceType(tbOrder.getSourceType());
            order.setCreateTime(new Date());

            order.setSellerId(cart.getSellerId());
            order.setPaymentType(tbOrder.getPaymentType());
            order.setReceiver(tbOrder.getReceiver());
            order.setReceiverAreaName(tbOrder.getReceiverAreaName());
            order.setReceiverMobile(tbOrder.getReceiverMobile());
            order.setStatus("1");

            //1.2.4 定义订单的总金额
            double sum = 0;
            //1.3 遍历历史订单明细
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //1.3.1 设置订单，明细的属性
                orderItem.setSellerId(cart.getSellerId());
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(nextId);
                //1.3.2 计算订单的总金额
                sum += orderItem.getTotalFee().doubleValue();
                //1.3.3 添加订单到数据库中
                orderItemMapper.insert(orderItem);

            }
            //设置订单总金额
            order.setPayment(new BigDecimal(sum));
            //保存订单
            orderMapper.insert(order);

            totalMoney += sum;
        }
        //判断当前订单的支付状态是否是1（微信支付），是就添加一天支付日志
        if (order.getPaymentType().equals("1")){
            //定义日志数据并设置值
            TbPayLog payLog =new TbPayLog();
            payLog.setCreateTime(new Date());
            payLog.setOrderList(ids.substring(0,ids.length()-1));
            payLog.setOutTradeNo(idWorker.nextId()+"");
            payLog.setPayType("1");
            payLog.setUserId(order.getUserId());
            payLog.setTradeState("0");                          //未支付
            payLog.setTotalFee((long)totalMoney*100);           //设置总金额
            //添加到数据库
            payLogMapper.insert(payLog);
            //存放到redis
            redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
        }

    }

    //2.根据用户名从redis中得到支付日志
    @Override
    public TbPayLog getPayLogFromRedis(String name) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(name);
    }

    //3.根据订单号修改支付日志及订单状态
    @Override
    public void updatePayLogAndOrder(String tradeNo, String transcation_id) {
        //3.1 根据主键查询支付日志
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(tradeNo);
        //3.2 修改支付日志信息
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transcation_id);
        payLog.setTradeState("1");              //表示已支付
        //3.3 修改支付日志
        payLogMapper.updateByPrimaryKey(payLog);

        //3.4 修改订单状态
        String orderList = payLog.getOrderList();
        for (String id : orderList.split(",")) {
            //3.4.1 根据id查询订单
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(new Long(id));
            //3.4.2 修改订单
            tbOrder.setStatus("2");         //表示已支付
            //3.4.3 保存到数据库
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        //3.5 从redis中删除支付日志
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
