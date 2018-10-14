app.controller("searchController", function ($scope, $location, searchService) {
    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//服务端返回的结果
                //调用分页栏
                buildPageLabel();
            }
        );
    };

    //$scope.searchMap.spec = {};
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 40,
        'sortField': '',
        'sort': ''
        //'totalPages': ''
    };//搜索对象
    //设置搜索选项
    $scope.addSearchItem = function (key, value) {
        //影响变量的值
        if (key == 'category' || key == 'brand' || key == 'price') {
            //如果点击的是分类或者是品牌列表或者是price价格区间
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索
    };

    //移除复合搜索条件
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = "";
        } else {//spec
            delete $scope.searchMap.spec[key];//移除这个属性
        }
        $scope.search();//执行搜索
    };

    //构建分页标签(totalPages为总页数)
    //$scope.resultMap.totalPages = [];
    var buildPageLabel = function () {
        $scope.pageLabel = [];//分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//得到最后的页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo;//截止页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后面有点
        if ($scope.resultMap.totalPages > 5) { //总页数大于5,显示部分
            if ($scope.searchMap.pageNo <= 3) {//前五后三?
                lastPage = 5;//前5页
                $scope.firstDot = false;//前面没有点
            } else if ($scope.searchMap.pageNo >= lastPage - 2) {
                firstPage = maxPageNo - 4;//后5页
                $scope.lastDot = false;//后面没有点
            } else {//中心5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
                $scope.firstDot = false;//前面没有点
                $scope.lastDot = false;//前面没有点
            }
        }
        //循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        //页码验证
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    };

    //判断当前页为第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    };

    //判断当前页是否未最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;//html中disable=true
        } else {
            return false;
        }
    };

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    };

    $scope.clear = function () {
        $scope.searchMap = {
            'keywords': $scope.searchMap.keywords,
            'category': '',
            'brand': '',
            'price': '',
            spec: {},
            'pageNo': 1,
            'pageSize': 40
        };
    };

    //设置排序规则
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    };

    //判断关键字是不是品牌
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
                $scope.searchMap.brand = $scope.resultMap.brandList[i].text;
                return true;
            }
        }
        return false;
    };

    //加载查询字符串
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        //alert();
        $scope.search();
    }


});