 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承

	//1.查询品牌列表
	$scope.findBrandList=()=>{
		brandService.findBrandList().success(
			function (response){
				$scope.brandList={"data":response};
				//console.log($scope.brandList);
			}
		)
	}

	//2.查询规格列表
	$scope.findSpecList=()=>{
		specificationService.findSpecList().success(
			function (response){
				$scope.specList={"data":response};
				//console.log($scope.specList);
			}
		)
	}

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//修改界面
	$scope.updateUI=(temp)=>{
		$scope.entity = temp;
		//将模板表中的json串转换为json对象
		$scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
		$scope.entity.specIds = JSON.parse($scope.entity.specIds);
		$scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
	}
	//处理显示效果
	$scope.showInfo=(list,key)=>{
		//将list转换为json对象
		list = JSON.parse(list);
		let info = "";		//最终连接的字符串
		for (let i = 0; i < list.length; i++) {
			info += list[i][key] + ","
		}
		//处理最后一个逗号
		return info.slice(0,info.length-1);
	}
    
});	
