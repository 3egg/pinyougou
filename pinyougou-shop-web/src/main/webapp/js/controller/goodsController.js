//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, uploadService,itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    };

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    //$scope.reloadList();//重新加载
                    alert("add succeed");
                    $scope.entity = {};
                } else {
                    alert(response.message);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //保存
    $scope.add = function () {
        //拿到富文本编辑框里面的数据交给goodsDesc.introduction
        $scope.entity.goodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    alert('保存成功');
                    $scope.entity = {}; //添加成功把页面的内容清空
                    editor.html('');//把富文本编辑器里面的内容清空
                } else {
                    alert(response.message);
                }
            }
        );
    };

    //上传文件的方法
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {//然后 Result
                if (response.success) {
                    //如果服务端成功就把url地址设置给img标签中的src属性
                    //这个message是 http://192.168.23.133:22122/group1/xxxxxx,sh
                    $scope.image_entity.url = response.message;//设置一个文件地址
                } else {
                    alert(response.message);
                }
            }
        ).error(
            function () {
                alert("upload error!");
            }
        )
    };

    //给页面上传图片的实体
    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};
    //添加图片列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };

    //给页面删除上传的选项
    $scope.remove_image_entity = function (index) {
        //根据索引把对应的行给删除了
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    };

    //读取商品分类的一级菜单
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {//List<TbItemCat>
                $scope.itemCat1List = response;
            }
        );
    };

    //读取二级菜单分类
    $scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
        //根据选择的值,查询二级分类
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            }
        );
    });

    //读取三级菜单分类
    //监听到了二级菜单的值发生了变化就改变三级菜单的值
    $scope.$watch('entity.goods.category2Id',function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        );
    });

    //读取模板id , 三级菜单一动就读取模板id
    $scope.$watch('entity.goods.category3Id',function (newValue, oldVlaue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        );
    })

});	
