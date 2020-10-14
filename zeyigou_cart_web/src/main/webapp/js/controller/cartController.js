app.controller("cartController",function ($scope,cartService){

    //1.查看购物车列表
    $scope.findCartList=()=>{
        cartService.findCartList().success(responce=>{
            $scope.cartList = responce;
            //计算总金额及总数量
            sum();
        })
    }

    //定义代表总金额及总数量的变量
    $scope.total={num:0,money:0};
    //2.计算总金额及总数量
    sum=()=>{
        //2.1 遍历购物车列表
        for (let i=0,len=$scope.cartList.length;i<len;i++){
            //2.2 得到一个个购物项
            let cart = $scope.cartList[i];
            //2.3 遍历购物项
            for (let j=0,len=cart.orderItemList.length;j<len;j++){
                //2.3.1 得到一个个商品
                let orderItem = cart.orderItemList[j];
                //2.3.2 计算总数量及总金额
                $scope.total.num += orderItem.num;
                $scope.total.money += orderItem.totalFee;
            }
        }
    }

    //3.添加到购物车
    $scope.addCartList=(itemId,num)=>{
        cartService.addCartList(itemId,num).success(responce=>{
            if (responce.success){
                $scope.findCartList();
            }else {

            }
        })
    }
    //4.查看用户的地址列表
    $scope.findAddressList=()=>{
        cartService.findAddressList().success(response=>{
            $scope.addressList = response;
            //如果当前的某个地址的isDefault为1，则将当前的正在遍历的对象赋值值$scope.addr
            for(let i = 0,len = response.length; i < len;i++){
                if(response[i].isDefault == 1){
                    $scope.addr = response[i];
                }
            }
        })
    }
    $scope.addr = {address:''};
    $scope.order = {paymentType:'1'};
    //5.用户点击某个姓名时，为全局变量赋值
    $scope.selectAddress=(addr)=>{
        $scope.addr = addr;
    }
    //6.根据当前遍历的地址，判断是否在上面$scope.address的属性中
    $scope.isSelected=(address)=>{
        return $scope.addr.address == address;
    }
    //7.保存订单
    $scope.save=()=>{
        //7.1)将用户所选择的地址放到order中
        $scope.order.receiverAreaName = $scope.addr.address;
        $scope.order.receiverMobile = $scope.addr.mobile;
        $scope.order.receiver = $scope.addr.contact;
        //7.2)将数据添加到表单中
        cartService.save($scope.order).success(response=>{
            if($scope.order.paymentType == 1){
                location.href = "pay.html";
            }else{
                location.href = "paysuccess.html";
            }
        })
    }
})