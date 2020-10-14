package com.zeyigou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.vientu.util.IdWorker;
import com.zeyigou.mapper.TbSeckillGoodsMapper;
import com.zeyigou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zeyigou.mapper.TbSeckillOrderMapper;
import com.zeyigou.pojo.TbSeckillOrderExample.Criteria;
import com.zeyigou.seckill.service.SeckillOrderService;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	//1.提交订单
	@Override
	public void submitOrder(String id, String name) {
		//第一步:处理秒杀商品的逻辑
		//1.1 查询秒杀商品
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillList").get(id);
		//1.2 判断商品是否存在
		if (seckillGoods==null){
			throw new RuntimeException("无此商品！");
		}
		if (seckillGoods.getStockCount()<=0){
			throw new RuntimeException("商品已秒空！");
		}
		//1.3 扣减库存
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
		//1.4 判断此时的秒杀商品的数量是否为0，如果为0，就从redis中删除，再修改到数据库中
		if (seckillGoods.getStockCount()==0){
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			redisTemplate.boundHashOps("seckillList").delete(id);
		}
		//1.5 重新放到redis中
		redisTemplate.boundHashOps("seckillList").put(id,seckillGoods);

		//第二步：处理秒杀订单的逻辑
		//2.1 定义秒杀订单对象
		TbSeckillOrder order = new TbSeckillOrder();
		order.setId(idWorker.nextId());
		order.setCreateTime(new Date());
		order.setMoney(seckillGoods.getCostPrice());
		order.setSellerId(seckillGoods.getSellerId());
		order.setStatus("0");					//未支付
		order.setUserId(name);
		order.setSeckillId(new Long(id));
		//2.2 将秒杀订单放到redis中
		redisTemplate.boundHashOps("seckillOrder").put(name,order);
	}

	//2.从redis中得到秒杀订单
	@Override
	public TbSeckillOrder getOrderFromRedis(String name) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(name);
	}

	//3.将订单从redis中保存到数据库中
	@Override
	public void saveOrderToDB(String name, String tradeNo, String transcation_id) {
		//3.1 从redis中得到订单
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(name);
		if (seckillOrder == null){
			throw new RuntimeException("订单不存在！");
		}
		//3.2 判断是否是同一订单
		if (!tradeNo.equals(seckillOrder.getId())){
			throw new RuntimeException("不是同一订单！");
		}

		//3.3 修改订单信息
		seckillOrder.setStatus("1");			//已支付
		seckillOrder.setPayTime(new Date());
		seckillOrder.setTransactionId(transcation_id);

		//3.4 保存订单到数据库中
		seckillOrderMapper.insert(seckillOrder);
		//3.5 从redis中删除订单
		redisTemplate.boundHashOps("seckillOrder").delete(name);
	}



}
