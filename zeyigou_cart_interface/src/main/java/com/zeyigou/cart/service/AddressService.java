package com.zeyigou.cart.service;

import com.zeyigou.pojo.TbAddress;

import java.util.List;

public interface AddressService {
    List<TbAddress> findAddressList(String name);
}
