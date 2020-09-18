app.controller("indexController",function ($scope,indexService){
    $scope.getUsername=()=>{
        indexService.getName().success(responce=>{
            $scope.username=responce.name;
            console.log($scope.username);
        })
    }
})