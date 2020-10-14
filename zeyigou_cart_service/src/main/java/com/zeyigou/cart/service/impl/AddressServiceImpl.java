package com.zeyigou.cart.service.impl;

import com.zeyigou.cart.service.AddressService;
import com.zeyigou.mapper.TbAddressMapper;
import com.zeyigou.pojo.TbAddress;
import com.zeyigou.pojo.TbAddressExample;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * AddressServiceImpl
 *
 * @Author Vientu
 * @Date 2020/10/5 23:57
 */
public class AddressServiceImpl implements AddressService {
    @Autowired
    private TbAddressMapper addressMapper;
    //1.根据用户id查询地址列表
    @Override
    public List<TbAddress> findAddressList(String name) {

        TbAddressExample example = new TbAddressExample();
        TbAddressExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(name);
        return addressMapper.selectByExample(example);
    }
}
