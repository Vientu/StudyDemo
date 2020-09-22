package com.zeyigou.content.service.impl;
import java.util.List;

import com.zeyigou.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zeyigou.mapper.TbContentMapper;
import com.zeyigou.pojo.TbContent;
import com.zeyigou.pojo.TbContentExample;
import com.zeyigou.pojo.TbContentExample.Criteria;
import com.zeyigou.content.service.ContentService;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		//1.添加前先删除Redis中的数据
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//1.获取原始的分类id
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		//2.删除原来的分类列表
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
		//3.再判断用户是否修改了分类，如果修改了分类，奖现在的分类广告列表也要删除
		if (categoryId.longValue()!=content.getCategoryId().longValue()){
			//3.1 删除修改后的分类列表
			redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
		}
		//4.修改广告
		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//1.根据广告id查询广告对象，从而根据广告对象得到分类id，再根据分类id从redis中删除分类列表
			TbContent tbContent = contentMapper.selectByPrimaryKey(id);
			redisTemplate.boundHashOps("contentList").delete(tbContent.getCategoryId());
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	//根据广告id查询广告列表
	@Override
	public List<TbContent> findCategory(Long cid) {
		//1.从redis中得到广告列表
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("contentList").get(cid);
		//2.如果不存在，就从数据库中查
		if (contentList==null||contentList.size()==0){
			//2.1 添加查询条件并查询
			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(cid);
			List<TbContent> tbContents = contentMapper.selectByExample(example);
			System.out.println("正在数据库中查询。。。");
			//2.2 添加到redis
			redisTemplate.boundHashOps("contentList").put(cid,contentList);
		}
		//3. 返回查询结果
		return null;
	}

}
