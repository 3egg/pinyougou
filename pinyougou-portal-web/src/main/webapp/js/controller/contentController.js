app.controller("contentController",function ($scope, contentService) {
   $scope.contentList = [];//广告的集合数据
   $scope.findByCategoryId = function (categoryId) {
       contentService.findByCategoryId(categoryId).success(
           function (response) {
               $scope.contentList[categoryId] = response;
           }
       );
   }
});