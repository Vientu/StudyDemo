app.controller("payController",function ($scope,$location,payService){
    //1.下单请求
    $scope.createNative=()=>{
        payService.createNative().success(responce=>{
            //1.1 得到订单总金额
            $scope.money = (responce.totalFee/100).toFixed(2);      //订单总金额
            //1.2 得到订单号
            $scope.tradeNo = responce.tradeNo;
            //1.3 生成二维码
            //1.3.1 得到返回的二维码的URL地址
            let codeUrl = responce.code_url;
            new QRious({
                element:document.getElementById("qr"),
                size:250,
                level:H,
                value:codeUrl
            })
            //1.3 每隔3s查询一次订单状态
            queryPayStatus($scope.tradeNo);
        })
    }

    //2 查询订单状态
    queryPayStatus=(tradeNo)=>{
        payService.queryPayStatus(tradeNo).success(responce=>{
            if (responce.success){
                location.href = "paysuccess.html#?money="+$scope.money;
            }else {
                if (responce.message.equals("二维码超时")){      //五分钟如果还没完成支付，就重新下单
                    $scope.createNative();
                }else {
                    location.href = "payfail.html"
                }
            }
        })
    }
    //3.支付成功页面显示订单金额
    $scope.showMoney=()=>{
        return $location.search()["money"];
    }

})