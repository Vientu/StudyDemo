package com.zeyigou.cart.service;

import com.zeyigou.group.Cart;

import java.util.List;

public interface CartService {
    List<Cart> getCartListFromRedis(String name);

    List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList);

    void saveCartListToRedis(List<Cart> redisCartList, String name);

    List<Cart> addCartList(List<Cart> cartList,Long itemId, int num);
}
