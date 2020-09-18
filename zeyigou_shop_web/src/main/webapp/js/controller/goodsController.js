 //控制层 
app.controller('goodsController' ,function($scope,$controller ,itemCatService,typeTemplateService ,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承

	//初始化初始化对象
	//$scope.entity = {goods:{},goodsDesc:{itemImages:[]},items:[]};
	//初始化组合对象
	$scope.entity={goods:{},goodsDesc: {itemImages: [],specificationItems:[]},items:[]};

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		$scope.entity.goodsDesc.introduction=editor.html();//得到富文本编辑器的内容并为introduction属性赋值
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	//$scope.reloadList();//重新加载
					alter("保存成功！");
					$scope.entity={};
					editor.html('');//清空富文本编辑器
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//定义文件上传方法
	$scope.updateFile=()=>{
		goodsService.updateFile().success(responce=>{
			if (responce.success){
				$scope.imageEntity.url = responce.message;
				console.log($scope.imageEntity.url);
			}else {
				alert(responce.message);
			}
		})
	}
	//将上传的对象放到$scope.entity.itemImages
	$scope.addImage=()=>{
		$scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
	}
    //1.根据父id查询子分类列表
	$scope.findByParentId=(parendId)=>{
		itemCatService.findByParentId(parendId).success(responce=>{
			$scope.category1List=responce;
		})
	}
	//2.根据一级分类查询二级分类列表（newValue：代表当前值，oldValue：代表上次的值）
	$scope.$watch("entity.goods.category1Id",(newValue,oldValue)=>{
		//查询二级分类列表
		itemCatService.findByParentId(newValue).success(responce=>{
			$scope.category2List = responce;
		})
	})
	//3.根据二级分类查询三级分类列表（newValue：代表当前值，oldValue：代表上次的值）
	$scope.$watch("entity.goods.category2Id",(newValue,oldValue)=>{
		//查询三级分类列表
		itemCatService.findByParentId(newValue).success(responce=>{
			$scope.category3List = responce;
		})
	})
	//4.根据三级分类id的变化得到模板id
	$scope.$watch("entity.goods.category3Id",(newValue,oldValue)=>{
		//4.1 根据分类id查出分类对象
		itemCatService.findOne(newValue).success(responce=>{
			//4.2 entity.goods.typeTemplateId赋值
			$scope.entity.goods.typeTemplateId = responce.typeId;
		})
	})
	//5.商品模板id的值发生变化，会得到不同的品牌
	$scope.$watch("entity.goods.typeTemplateId",(newValue,oldValue)=>{
		typeTemplateService.findOne(newValue).success(responce=>{
			//5.1 根据模板id查询模板对象，从而查询出品牌列表
			$scope.brandList = JSON.parse(responce.brandIds);
			//5.2 根据模板对象，从而查出自定义扩展属性
			$scope.entity.goodsDesc.customAttributeItems = JSON.parse(responce.customAttributeItems)
			//5.3 根据模板对象，查询出规格列表（需要带有规格选项列表）
			//$scope.specList= JSON.parse(responce.specIds);
			typeTemplateService.findSpecList(newValue).success(responce=>{
				$scope.specList = responce;
			})
		})
	})
	//6.当用户选择某个规格选项时
	$scope.updateSpec=(event,name,value)=> {
		//6.1)在goodsDesc表的specificationItems字段查询是否有指定的点击对象
		let obj = searchByKey($scope.entity.goodsDesc.specificationItems, "attributeName", name);

		//6.2)如果obj没有值，代表某个规格及其选项是第一次添加到$scope.entity.goodsDesc.specificationItems
		if(!obj){
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}else{	//6.3)如果有值，再根据复选状态，决定是添加到此集合还是从中删除
			if(event.target.checked){		//被复选：event.target： 代表复选框控件
				obj.attributeValue.push(value);
			}else{						    //未被复选
				//6.4)当前未被复选时，从中删除此元素
				let index = obj.attributeValue.indexOf(value);
				obj.attributeValue.splice(index,1);
				//6.5)判断一下，此元素的个数是否为0，如果为0，证明一个元素也没有,此时就要将当前的attributeValue
				// 从大集合$scope.entity.goodsDesc.specificationItems中删除
				if(obj.attributeValue.length == 0){
					let vIndex = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
					$scope.entity.goodsDesc.specificationItems.splice(vIndex,1);
				}
			}
		}
	createSkuList();
	}
	//7.根据数组的key与value查询数组中是否有此对象
	searchByKey=(list,key,value)=>{
		for(let i = 0,len = list.length; i < len;i++){
			if(list[i][key] == value){
				return list[i];
			}
		}
		return null;
	}
	//8.生成sku列表
	createSkuList=()=>{
		//8.1)定义sku默认值
		$scope.entity.items = [{spec:{},price:0,num:99999,status:'0',isDefault:'0' }];
		//8.2)得到用户选择的规格及选项数据
		let items = $scope.entity.goodsDesc.specificationItems;
		//8.3)遍历此数据，向$scope.entity.items中添加数据
		for (let i = 0; i < items.length; i++) {
			$scope.entity.items = addData($scope.entity.items,items[i].attributeName,items[i].attributeValue);
		}
	}
	//9.定义添加数据的方法
	addData=(list,name,value)=>{
		//9.1)定义存放数据的数组
		let itemList = [];
		//9.2)遍历list集合将name及value中的值传入其中
		for (let i = 0; i < list.length; i++) {
			//9.2.1)得到原始行数据
			let oldRow = list[i];
			for(let j = 0;j < value.length;j++){
				//9.2.2)根据原始行克隆出新行
				let newRow = JSON.parse(JSON.stringify(oldRow));
				//9.2.3)在新行中添加规格数据
				newRow.spec[name] = value[j];
				//9.2.4)向新行中添加数据
				itemList.push(newRow);
			}
		}
		//9.3)返回
		return itemList;
	}
});	
