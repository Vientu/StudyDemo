app.controller("searchController",function ($scope,searchService){

    $scope.search=()=>{
        searchService.search($scope.searchMap).success(responce=>{
            $scope.resultMap=responce;
            console.log($scope.resultMap);
        })
    }
})