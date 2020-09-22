package com.vientu.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-redis.xml")
public class TestSpringDataRedis {

    @Autowired
    private RedisTemplate redisTemplate;
    //1.操作字符串
    @Test
    public void test01(){
        //1.1)向redis中存放字符串
        redisTemplate.boundValueOps("name").set("张三");
        //1.2)从redis中取得字符串
        String name = (String) redisTemplate.boundValueOps("name").get();
        //1.3)打印
        System.out.println("name = " + name);
//        //1.4)删除
//        redisTemplate.delete("name");
//        //1.5）再取值
//        name = (String) redisTemplate.boundValueOps("name").get();
//        //1.6)再打印
//        System.out.println("name = " + name);
    }

    //2.操作set集合
    @Test
    public void test02(){
        //2.1)向redis中存放set数据
        redisTemplate.boundSetOps("names").add("张飞");
        redisTemplate.boundSetOps("names").add("赵云","关羽","吕布");
        redisTemplate.boundSetOps("names").add("曹操");

        //2.2)从redis中取得set的值
        Set names = redisTemplate.boundSetOps("names").members();
        names.forEach(a->{
            System.out.println(a);
        });
        System.out.println("---------------------------------------");

        //2.3)删除一个值
        redisTemplate.boundSetOps("names").remove("赵云");
        //2.4)再一次得到值
        names = redisTemplate.boundSetOps("names").members();
        //2.5)再一次打印
        names.forEach(System.out::println);
        System.out.println("---------------------------------------");

//        //2.6)删除所有值
//        redisTemplate.delete("names");
//        //2.7)再一次得到值
//        names = redisTemplate.boundSetOps("names").members();
//        //2.8)再一次打印
//        names.forEach(System.out::println);
    }

    //3.redis操作list集合
    @Test
    public void test03(){
        //3.1)左入栈
        redisTemplate.boundListOps("names").leftPushAll("张三","李四","王五");
        //3.2)右入栈
        redisTemplate.boundListOps("names").rightPushAll("小明","小红","小马") ;
        //3.3)打印
        List names = redisTemplate.boundListOps("names").range(0, -1);
        for (Object name : names) {
            System.out.println(name);
        }
        System.out.println("-------------------------------------");
    }

    //4.redis操作hash
    @Test
    public void test04(){
        //4.1)向hash中添加内容
        redisTemplate.boundHashOps("studMap").put("sid",1001);
        redisTemplate.boundHashOps("studMap").put("sname","男");
        redisTemplate.boundHashOps("studMap").put("addr","上海");

        //4.2)取出key的集合
        Set keys = redisTemplate.boundHashOps("studMap").keys();
        keys.forEach(key -> {
            System.out.println(key);
        });
        System.out.println("-------------------------------------");

        //4.3)取出value的集合
        List studMap = redisTemplate.boundHashOps("studMap").values();
        studMap.forEach(System.out::println);

    }
}
