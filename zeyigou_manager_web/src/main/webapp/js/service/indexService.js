app.service("indexService",function ($http){
    //1. 获得用户名
    this.getName=()=>{
        return $http.get("../index/getName.do");
    }
})