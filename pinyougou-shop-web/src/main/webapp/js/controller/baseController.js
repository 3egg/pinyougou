//品牌控制层
app.controller('baseController', function ($scope) {

    //重新加载列表 数据
    $scope.reloadList = function () {
        //切换页码
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    $scope.selectIds = [];//选中的ID集合

    //更新复选
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//如果是被选中,则增加到数组
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
        }
    };

    //提供给json字符串,返回拼接后面的字符串,逗号分割
    $scope.jsonToString = function (jsonString, key) {
        //因为服务端返回给页面的是json形式的字符串
        //将这个字符串转换为json对象
        var json = JSON.parse(jsonString);
        //然后定义新的字符串拼接
        var value = "";
        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += ",";
            }
            //拼接字符串
            value += json[i][key];
        }
        return value;
        /*//或者
        var str = "";
        for (var j = 0; j < json.length; j++) {//[{id:1,text:xx},{id:1,text:xx}...]
            var obj = json[i];//obj 就是json对象里面的一个{id:1,text:xx}
            str += obj[key] + ",";//通过页面的传进来的key,如text就获取到里面的xx这个数据
        }
        if (str.length > 0) {//解决最后一个逗号多余的问题
            str = str.substring(0, str.length - 1);//把最后一位移除
        }
        return str;//最后返回这个str拼接好的后字符串*/
    }

});	