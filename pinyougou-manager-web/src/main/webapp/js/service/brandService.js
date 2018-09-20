//service层负责向服务端发送请求
//service只负责发请求,不负责服务端的回调函数
//品牌服务层
app.service('brandService', function ($http) {
    //读取列表数据绑定到表单中
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    };
    //分页
    this.findPage = function (page, rows) {
        return $http.get('../brand/findPage.do?page=' + page + '&rows=' + rows);
    };
    //查询实体
    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id);
    };
    //增加
    this.add = function (entity) {
        //使用post请求,因为有中文要解决中文乱码
        return $http.post('../brand/add.do', entity);
    };
    //修改
    this.update = function (entity) {
        //修改依然,也要解决中文乱码
        return $http.post('../brand/update.do', entity);
    };
    //删除
    this.dele = function (ids) {
        return $http.get('../brand/delete.do?ids=' + ids);
    };
    //搜索
    this.search = function (page, rows, searchEntity) {
        //搜索也是
        return $http.post('../brand/search.do?page=' + page + "&rows=" + rows, searchEntity);
    }

});
