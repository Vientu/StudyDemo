app.controller("searchController",function ($scope,$location,searchService){

    $scope.searchMap = {'keywords':'','category':'','brand':'','spec':{},
                        'price':'','sort':'','sortField':'','page':1,'pageSize':40};

    //1.根据查询参数向后台发出请求
    $scope.search=()=>{
        //6.1 将从主页传入的值 赋给当前的搜索框
        let keywords = $location.search()["keywords"];
        if (keywords){
            $scope.searchMap.keywords = keywords;
        }
        searchService.search($scope.searchMap).success(responce=>{
            $scope.resultMap=responce;
        })
    }

    //2.添加指定对象到$scope.searchMap中
    $scope.addSearchItem=(key,value)=>{
        if (key == 'brand' || key == 'category'){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }
    //3.移除搜索项
    $scope.removeSearchItem=(key)=>{
        if (key == 'brand' || key == 'category'){
            $scope.searchMap[key]='';
        }else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
    //4.排序
    $scope.addSortSearch=(sort,sortField)=>{
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }
    //5.隐藏品牌列表（只要搜索的关键字中包含有指定品牌，则隐藏品牌列表
    $scope.hideBrandList=()=>{
        //5.1 得到后台发来的所有的品牌列表
        let brandList = $scope.resultMap.brandList;
        //5.2 遍历品牌列表，找到某个品牌与用户输入的关键字进行对比
        for(let i=0,len=brandList.length;i<len;i++){
            if (brandList[i].text.indexOf($scope.searchMap.keywords)>=0){
                return true;
            }
        }
        return false;
    }
    //6.生成分页导航
    createPageNav=()=>{
        //6.1 定义存放分页标号的数组
        $scope.pageLabel=[];
        //6.2 根据情况进行分页
        //6.2.1 设置开始页
        let firstPage = 1;
        //6.2.2 设置结束页
        let lastPage = $scope.resultMap.totalPage;
        //6.2.3 定义代表前后省略号的变量
        $scope.firstDot = true;
        $scope.lastDot = false;
        //6.2.4 处理首页与尾页
        if ($scope.resultMap.totalPage>5){
            if ($scope.searchMap.page<=3){
                lastPage = 5;
                $scope.firstDot = false;
            }else if ($scope.searchMap.page>=$scope.resultMap.totalPage-2){
                firstPage = $scope.resultMap.totalPage-4;
                $scope.lastDot = false;
            }else {
                firstPage = $scope.searchMap.page-2;
                lastPage = $scope.searchMap.page+2
            }
        }
        //6.2.5 向分页标号数组中添加标号
        for (let i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i)
        }
    }
})
