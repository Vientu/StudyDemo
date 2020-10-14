package com.zeyigou.users.service;

import com.zeyigou.pojo.TbUser;

public interface UserService {
    void getValidCode(String phone);

    boolean isValide(String phone, String validCode);

    void add(TbUser user);
}
