//定义分页控制器
app.controller("baseController",function ($scope,) {
    //重新加载列表，数据
    $scope.reloadList=function (){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }
    //定义分页配置
    $scope.paginationConf = {
        currentPage: 1,			//当前页
        totalItems: 10,			//总记录数
        itemsPerPage: 5,			//每页的记录数
        perPageOptions: [10, 20, 30, 40, 50],	//分页选项
        onChange: function () {
            $scope.reloadList();			//带条件分页查询
        }
    };

    $scope.selectIds=[];        //选中的id数组

    //重新复选
    // 根据用户对复选框的选中状态决定是否将数据放到$scope.selectIds中
    $scope.updateSelection=($event,id)=>{
        if ($event.target.checked){			//如果复选框被选中
            // 添加到数组
            $scope.selectIds.push(id);
        }else {							//如果未被选中则从数组中删除此元素
            // 根据id找到此id在数组中的下标位置
            let index = $scope.selectIds.indexOf(id);
            // 根据下标从数组中删除
            $scope.selectIds.splice(index,1);
        }
    }
});