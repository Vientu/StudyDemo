app.service("searchService",function ($http){

    //1.根据分类查询此分类列表
    this.search=(searchMap)=>{
        return $http.post("./itemSearch/search.do",searchMap);
    }
})