package com.zeyigou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zeyigou.cart.service.CartService;
import com.zeyigou.group.Cart;
import com.zeyigou.mapper.TbItemMapper;
import com.zeyigou.pojo.TbItem;
import com.zeyigou.pojo.TbOrderItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * CartServiceImpl
 *
 * @Author Vientu
 * @Date 2020/9/30 17:09
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    //1.从Redis中得到购物车数据
    @Override
    public List<Cart> getCartListFromRedis(String name) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(name);
        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    //2.合并数据
    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        for (Cart cart : cookieCartList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                redisCartList = addCartList(redisCartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisCartList;
    }

    //3.保存数据到redis
    @Override
    public void saveCartListToRedis(List<Cart> redisCartList, String name) {
        redisTemplate.boundHashOps("cartList").put(name,redisCartList);
    }

    //4.添加商品到购物车
    @Override
    public List<Cart> addCartList(List<Cart> cartList,Long itemId,int num){
        //4.1 根据商品id查询商品
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //4.2 根据商家id查询购物车对象
        Cart cart = findCartBySellerId(tbItem.getSellerId(),cartList);
        //4.3 如果不存在购物车，就创建此购物车,将其添加到购物车列表中
        if(cart == null){
            cart = createCart(tbItem,num);
            cartList.add(cart);
        }else{  //4.4 如果存在购物车，就修改购物数量及小计
            //4.4.1)判断要添加的商品是否在购物项列表中
            TbOrderItem orderItem = findOrderItem(cart.getOrderItemList(),itemId);
            //4.4.2)判断此商品项是否存在
            if(orderItem == null){
                //① 如果不存在，就创建一个新的商品项，
                orderItem = createOrderItem(tbItem, num);
                //② 并添加到此购物车的商品项列表中
                cart.getOrderItemList().add(orderItem);
            }else{  //4.4.3）如果存在此商品项，就修改数量及小计
                orderItem.setNum(orderItem.getNum() + num);     //修改数量
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));  //修改小计

                //4.4.4)如果商品的数量为0，就从此商品列列表中删除此商品
                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //4.4.5)如果此购物车的购物项集合为0，此时就从购物车集合中删除此购物车
                if(cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    //5.根据商家id查询是否存在购物车
    private Cart findCartBySellerId(String sellerId, List<Cart> cartList) {
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerId())){
                return cart;
            }
        }
        return null;
    }

    //6.创建购物车
    private Cart createCart(TbItem tbItem, int num) {
        //6.1 构造一个cart对象
        Cart cart = new Cart();
        //6.2 设置属性
        cart.setSellerId(tbItem.getSellerId());
        cart.setSellerName(tbItem.getSeller());
        //6.3 设置购物车关联的订单项集合
        List<TbOrderItem> orderItemList = new ArrayList<>();
        //6.4 创建订单项
        TbOrderItem orderItem = createOrderItem(tbItem,num);
        //6.5 将订单添加到订单集合中
        orderItemList.add(orderItem);
        //6.6 返回购物车
        return cart;
    }

    //7.根据商品id及购物项集合查询在此集合中是存在此商品
    private TbOrderItem findOrderItem(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    //8.创建订单项
    private TbOrderItem createOrderItem(TbItem tbItem, int num) {
        //8.1 定义一个订单项对象
        TbOrderItem orderItem = new TbOrderItem();
        //8.2 设置订单项的各个属性
        orderItem.setGoodsId(tbItem.getGoodsId());
        orderItem.setItemId(tbItem.getId());
        orderItem.setSellerId(tbItem.getSellerId());
        orderItem.setNum(num);
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setPrice(tbItem.getPrice());
        orderItem.setTitle(tbItem.getTitle());
        //8.3 设置小计
        BigDecimal totalFee = new BigDecimal(tbItem.getPrice().doubleValue()*num);
        orderItem.setTotalFee(totalFee);
        //8.3 返回
        return orderItem;
    }
}
