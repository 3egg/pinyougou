var app=angular.module('pinyougou',[]);

/*$sce服务,过滤器服务*/
app.filter('trustHtml',['$sce',function ($sce) {
    return function (data) {//data表示原来的html代码
        return $sce.trustAsHtml(data);//返回过来后信任的代码
    }
}]);

/* 这种写法也可以
app.filter('trustHtml',function ($sce) {
   return function (data) {
       return $sce.trustAsHtml(data);
   }
});*/
