package com.vientu.util;

import com.zeyigou.mapper.TbSeckillGoodsMapper;
import com.zeyigou.pojo.TbSeckillGoods;
import com.zeyigou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * SpringTaskUtil
 *
 * @Author Vientu
 * @Date 2020/10/10 17:51
 */
@Component
public class SpringTaskUtil {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //1.springtask的基本用法
    @Scheduled(cron = "0/5 * * * * ?")
    public void test01(){
        System.out.println("现在的时间是："+new Date());
    }

    //2.每隔一分钟同步一下数据库与redis的秒杀商品
    @Scheduled(cron = "0 * * * * ?")
    public void test02(){
        //2.1 查询Redis中的秒杀商品的id列表
        Set keys = redisTemplate.boundHashOps("seckillList").keys();
        //2.2 得到在redis中的秒杀商品列表
        List ids = new ArrayList(keys);
        //2.3 再从数据库中查询不在redis中的商品
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThan(new Date());
        criteria.andStatusEqualTo("1");
        criteria.andStockCountGreaterThan(0);
        criteria.andIdNotIn(ids);
        //2.4 查询出Redis中没有秒杀商品
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        //2.5 将商品一件件放到redis中
        for (TbSeckillGoods seckillGood : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillList").put(seckillGood.getId(),seckillGood);
        }
    }

    //3.每隔一秒，查询是否有过期数据，如果有就从redis中删除
    @Scheduled(cron = "* * * * * ?")
    public void test03(){
        //3.1 得到redis中所有秒杀商品
        List<TbSeckillGoods> seckillList = redisTemplate.boundHashOps("seckillList").values();
        //3.2 遍历商品列表，并比较到期时间，看是否小于或等于当前时间
        for (TbSeckillGoods goods : seckillList) {
            if (goods.getEndTime().getTime()<=new Date().getTime()){
                //3.2.1 先将商品保存到数据库
                seckillGoodsMapper.updateByPrimaryKey(goods);
                //3.2.2 从redis中删除到期商品
                redisTemplate.boundHashOps("seckillList").delete(goods.getId());
                System.out.println("正在从redis中删除过期商品，goodId="+goods.getId());
            }
        }
    }
}

