package com.zeyigou.manager.controller;
import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.zeyigou.group.Goods;
import com.zeyigou.page.service.PageService;
import com.zeyigou.pojo.PageResult;
import com.zeyigou.pojo.Result;
import com.zeyigou.pojo.TbGoods;
import com.zeyigou.pojo.TbItem;
import com.zeyigou.sellgoods.service.GoodsService;
import com.zeyigou.sellgoods.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;

import javax.jms.*;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	@Reference
	private ItemService itemService;
//	@Reference
//	private PageService pageService;
//	@Reference
//	private ItemSearchService itemSearchService;

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Queue updateStatusQueue;
	@Autowired
	private Queue deleteQueue;
	@Autowired
	private Topic genHtmlTopic;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(sellerId);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			//删除索引库中的内容
			//searchService.deleteIndexByGoodsId(ids);

			//1.搜索服务发送jms消息，用于从索引库中删除指定的id的商品
			jmsTemplate.send(deleteQueue, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 审核
	 */
	@RequestMapping("updateStatus")
	public Result upadteStatus(String  status,Long[] ids){
		try {
			if (status.equals("1")){ //代表审核通过
				//1.根据审核的商品id，查询sku列表
				List<TbItem> tbItems = itemService.findItemsByGoodsId(ids);
				//2.判断是否得到数据，如果有就导入索引库中
				if (tbItems!=null && tbItems.size()>0){
					//goodsService.upadteStatus(status,ids);
					//使用jms发送消息
					jmsTemplate.send(updateStatusQueue, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(JSON.toJSONString(tbItems));
						}
					});
				}
			}
			goodsService.upadteStatus(status,ids);
			return new Result(true,"审核成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"审核失败！");
		}
	}

	//1.根据商品id生成页面静态化
	@RequestMapping("genHtml")
	public void genHtml(Long goodsId) throws IOException {
		//pageService.genHtml(goodsId);
		//1.向生成静态页面的服务pageService发送消息
		jmsTemplate.send(genHtmlTopic, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(goodsId+"");
			}
		});
	}
}
