app.controller("userController",function ($scope,userService){

    //1.获取用户的验证码
    $scope.getValidCode=()=>{
        userService.getValidCode($scope.entity.phone).success(responce=>{
            alert(responce.message);
        })
    }

    //2.添加用户
    $scope.add=()=>{
        if($scope.entity.password!=$scope.repassword){
            alert("两次密码不一致！");
            return ;
        }
        //注册用户
        userService.add=($scope.entity,$scope.validCode).success(responce=>{
            alert(responce.message);
        })
    }

    //3.得到用户名
    $scope.getName=()=>{
        userService.getName().success(responce=>{
            $scope.name = responce.name;
        })
    }

})