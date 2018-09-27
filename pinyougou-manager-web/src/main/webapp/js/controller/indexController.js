app.controller('indexController', function ($scope, $controller, loginService) {

    //读取当前登陆人
    $scope.showLoginName = function () {
        //这里的loginService访问的是../login/name.do
        //可以从security的上下文中得到认证后的用户信息即可以getName()
        loginService.loginName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }

});