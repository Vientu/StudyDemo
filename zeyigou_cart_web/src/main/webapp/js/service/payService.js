app.service("payService",function ($http){
    //1.下单请求
    this.createNative=()=>{
        return $http.get("./pay/createNative.do");
    }

    //2.查看订单支付状态
    this.queryPayStatus=(tradeNo)=>{
        return $http.get("./pay/queryPayStatus.do?tradeNo="+tradeNo);
    }
})