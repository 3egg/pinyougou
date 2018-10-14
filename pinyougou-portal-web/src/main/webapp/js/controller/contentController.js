app.controller("contentController",function ($scope, contentService) {
   $scope.contentList = [];//广告的集合数据
   $scope.findByCategoryId = function (categoryId) {
       contentService.findByCategoryId(categoryId).success(
           function (response) {
               $scope.contentList[categoryId] = response;
           }
       );
   };

    //搜索跳转
    $scope.search=function(){
        window.location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }

});