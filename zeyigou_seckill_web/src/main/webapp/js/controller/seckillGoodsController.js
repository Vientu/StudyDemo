 //控制层 
app.controller('seckillGoodsController' ,function($scope,$controller,$interval,$location,seckillGoodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		seckillGoodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		seckillGoodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		seckillGoodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=seckillGoodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=seckillGoodsService.add( $scope.entity  );//增加 
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
		seckillGoodsService.dele( $scope.selectIds ).success(
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
		seckillGoodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//跳转到详情页
	$scope.showDetail=(id)=>{
		location.href = "seckill-item.html#?id="+id;
	}

	//$interval进行倒计时显示
	showTime=()=>{
		//1.计算总共余下的秒数
		let times = Math.floor(((new Date($scope.entity.endTime)).getTime()-(new Date().getTime()))/1000);
		//2.定义显示的字符串
		$scope.info = "";
		//3.计算天数，小时数，分钟数，秒数变量
		//3.1 计算天数
		let days = Math.floor(times/(3600*24));
		//3.2 计算小时数
		let hours = Math.floor((times-days*3600*24)/3600);
		//3.3 计算分钟数
		let minutes = Math.floor((times-days*3600*24-hours*3600)/60);
		//3.4 计算秒数
		let seconds = times-days*3600*24-hours*3600-minutes*60;
		//4.拼接字符串
		if (days>0){
			$scope.info += days + "天 ";
		}
		$scope.info += hours+":"+minutes+":"+seconds;
	}
	//使用$interval调用方法
	$interval(()=>{
		showTime();
	},1000)

	//提交订单
	$scope.submitorder=()=>{
		seckillGoodsService.submitorder($scope.entity.id).success(responce=>{
			if (responce.success){
				alert("下单成功，请在1分钟内完成支付！")
				location.href = "pay.html";
			}else {
				alert(responce.message);
			}
		})
	}
});	
