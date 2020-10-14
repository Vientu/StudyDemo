app.service("cartService",function ($http){
    //1.查看购物车列表
    this.findCartList=()=>{
        return $http.get("./cart/findCartList.do");
    }

    //2.添加到购物车
    this.addCartList=(itemId,num)=>{
        return $http.get("./cart/addCartList.do?itemId"+itemId+"&num="+num);
    }
})