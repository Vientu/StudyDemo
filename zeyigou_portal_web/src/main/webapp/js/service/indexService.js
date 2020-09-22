app.service("indexService",function ($http){

    //1.根据分类查询此分类列表
    this.findCategoryList=(cid)=>{
        $http.get("./index.do?cid="+cid);
    }
})