package com.zeyigou.shop.service;

import com.zeyigou.pojo.TbSeller;
import com.zeyigou.sellgoods.service.SellerService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserDetailsService {
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService){
        this.sellerService = sellerService;
    }
    @Override
    public UserDetails loadUserByUsername(String sellerId) throws UsernameNotFoundException {
        //1.得到当前用户
        TbSeller seller = sellerService.findOne(sellerId);
        //2.如果用户存在，并且状态为1，就得到其角色列表
        if (seller !=null && seller.getStatus().equals("1")){
            //2.1 定义存在存放角色的集合
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
            //2.2 返回用户对象
            return new User(sellerId,seller.getPassword(),authorities);
        }
        return null;
    }
}
