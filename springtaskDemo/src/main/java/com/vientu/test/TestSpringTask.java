package com.vientu.test;

import com.vientu.util.SpringTaskUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * TestSpringTask
 *
 * @Author Vientu
 * @Date 2020/10/10 17:58
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class TestSpringTask {
    @Autowired
    private SpringTaskUtil taskUtil;

    //1.springtask的基本用法
    @Test
    public void test01() throws IOException {
        taskUtil.test01();
        System.in.read();
    }
    //2.测试redis与数据库的同步
    @Test
    public void test02() throws IOException {
        taskUtil.test02();
        System.in.read();
    }
    //3.测试过期商品的删除
    @Test
    public void test03() throws IOException {
        taskUtil.test03();
        System.in.read();
    }
}
