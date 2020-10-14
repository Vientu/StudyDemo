 //控制层
app.controller('itemController' ,function($scope,$http,$controller ){

	//1.绑定一个num对象
	$scope.num = 1;
	//2.点击添加或减少按钮会添加或减少变量num
	$scope.addNum=(n)=>{
		$scope.num = $scope.num + n;
		if($scope.num <= 1){
			$scope.num = 1;
		}
	}

	//3.定义用户选择的规格对象
	$scope.specItem = {};
	//4.定义用户点击了某个规格时执行的代码
	$scope.selectSpec=(k,v)=>{
		$scope.specItem[k] = v;
		userSelect();
	}
	//5.设置样式属性时执行的方法
	$scope.isSelected=(key,value)=>{
		return $scope.specItem[key] == value;
	}


	//6.窗体加载时显示sku商品
	$scope.loadSku=()=>{
		$scope.sku = skuList[0];
		$scope.specItem=JSON.parse(JSON.stringify($scope.sku.spec));
	}
	//7.比较两个对象是否相等
	matchObject=(map1,map2)=>{
		for(var i in map1){
			if(map1[i] != map2[i]){
				return false;
			}
		}
		for(var i in map2){
			if(map2[i] != map1[i]){
				return false;
			}
		}
		return true;
	}
	

	//8.当用户点击某个规格选项时，会比较用户点击的哪个spec对象与全局的sku列表（即skuList）中的某个spec对象是否相等，如果
	//相等，就找到这个spec所在的对象，并赋值给$scope.sku对象
	userSelect=()=>{
		console.log("specItem:" + $scope.specItem);
		//console.log("specItem:" + $scope.specItem);
		for(let i = 0;i < skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specItem)){
				$scope.sku = skuList[i];
				return;
			}
		}
	}
	//添加到购物车
	$scope.addCart=()=>{
		$http.get("http://localhost:9107/cart/addCartList.do?itemId="+$scope.sku.id+"&num="+$scope.num,
			{'withCredentials':true}).success(response=>{
			if(response.success){
				location.href = "http://localhost:9107/cart.html";
			}else{
				alert(response.message);
			}
		})
	}
});	
