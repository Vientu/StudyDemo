app.controller("itemController",function ($scope,itemService){
    //绑定一个num
    $scope.num =1;

    //点击减少或增加num
    $scope.addNum=(n)=>{
        $scope.num = $scope.num + n;
        if ($scope.num<=1){
            $scope.num=1;
        }
    }

    //窗体加载时加载sku
    $scope.loadSku=()=>{
        $scope.sku=skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    }

    //比较两个对象是否相等
    $scope.matchObject=(map1,map2)=>{
        for (var i in map1){
            if (map1[i]!=map2[i]){
                return false;
            }
        }
        for (var i in map2){
            if (map2[i]!=map1[i]){
                return false;
            }
        }
        return true;
    }

    //定义用户选择的规格对象
    $scope.specItem = {};

    //定义用户点击某个规格时执行的代码
    $scope.selectSpec=(k,v)=>{
        $scope.specItem[key]=v;
        $scope.userSelectSpec();
    }

    //设置样式时执行的方法
    $scope.isSelected=(key,value)=>{
        return $scope.specItem[key]=value;
    }

    //判断点击规格时哪个规格是否与skuList中的某个spec是否相等
    $scope.userSelectSpec=function(){
        for(var i = 0;i < skuList.length;i++){
            if($scope.matchObject($scope.specificationItems,skuList[i].spec)){
                $scope.sku=skuList[i];
                return;
            }
        }
    }

})