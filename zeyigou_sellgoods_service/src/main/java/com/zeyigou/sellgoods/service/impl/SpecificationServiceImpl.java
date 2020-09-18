package com.zeyigou.sellgoods.service.impl;
import java.util.List;
import java.util.Map;

import com.zeyigou.group.Specification;
import com.zeyigou.mapper.TbSpecificationMapper;
import com.zeyigou.mapper.TbSpecificationOptionMapper;
import com.zeyigou.pojo.*;
import com.zeyigou.sellgoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//1.添加规格
		specificationMapper.insert(specification.getSpec());
		//2.添加规格选项
		//2.1 得到规格选项
		List<TbSpecificationOption> options = specification.getSpecificationOptionList();
		//2.2 遍历规格选项
		for (TbSpecificationOption option : options) {
			//2.3 绑定规格选项的外键
			option.setSpecId(specification.getSpec().getId());
			//2.4 添加规格选项
			specificationOptionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//1. 修改规格
		specificationMapper.updateByPrimaryKey(specification.getSpec());
		//2. 修改规格选项
		//2.1 首相根据外键来删除选项表中的数据
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpec().getId());
		specificationOptionMapper.deleteByExample(example);
		//2.2 在向规格表添加数据
		List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptionList();
		for (TbSpecificationOption specificationOption : specificationOptions) {
			//2.2.1 绑定外键
			specificationOption.setSpecId(specification.getSpec().getId());
			//2.2.2 添加
			specificationOptionMapper.insert(specificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//1.根据规格id查出规格对象
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

		//2.根据规格id查出规格选项对象
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(tbSpecification.getId());
		List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(example);

		//3.定义组合对象
		Specification specification = new Specification();
		specification.setSpec(tbSpecification);
		specification.setSpecificationOptionList(specificationOptions);

		//4.返回
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		TbSpecificationExample.Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> findSpecList() {
		return specificationMapper.findSpecList();
	}

}
