package com.zeyigou.page.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * PageService
 *
 * @Author Vientu
 * @Date 2020/9/24 16:55
 */
public interface PageService {
    void genHtml(Long goodsId) throws IOException;
}
