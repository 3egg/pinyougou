app.service("searchService",function ($http) {
    this.search = function (searchMap) {//因为向服务端传递的是一个map就不要用get请求了
        return $http.post("../itemSearch/search.do",searchMap);
    }
});