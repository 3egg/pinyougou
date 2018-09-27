//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, uploadService, itemCatService, typeTemplateService) {

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
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
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
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //根据选择的值,查询二级分类
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
                $scope.itemCat3List = null;
            }
        );
    });

    //读取三级菜单分类
    //监听到了二级菜单的值发生了变化就改变三级菜单的值
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        );
    });

    //读取模板id , 三级菜单一动就读取模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldVlaue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        );
    });

    //模板id有值了就根据模板id找到品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                //获取模板类型,得到里面额brand_id数据
                //brand_id数据格式为 [{id:1,text:联想},{...},...]
                $scope.typeTemplate = response;
                //获取到了品牌列表
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                //读取模板信息时,也要获取到扩展属性
                //页面展示的是自定义goods 里面包含了goodsDesc,goods和List<TbItem>数据
                //这里就把typeTemplate里面的扩展属性数据转换成json,
                //然后页面展示就用的是goods.goodsDesc.customAttribute扩展属性
                //[{"text":"内存大小"},{"text":"颜色"}]
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
            }
        );

        //模板即typeTemplate有值了,就查询规格列表,这里是用于页面显示模板的规格
        typeTemplateService.findSpecList(newValue).success(
            function (response) {//List<Map>
                $scope.specList = response;
            }
        );
    });

    //这是用动态把页面的显示内容,准备好数据提交给服务端
    //这里的entity的goodsDesc数据先设置为null
    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};
    //最终向服务端传递的数据格式为???
    $scope.updateSpecAttribute = function ($event, name, value) {
        var object = $scope.searchObjectByKey(
            $scope.entity.goodsDesc.specificationItems, 'attributeName', name
        );
        //这里返回的对象object格式为: [{"attributeName":"网络","attributeValue":["移动3G"]}]
        //如果有值返回的就是"attributeValue":["移动3G"] attributeValue是一个数组
        if (object != null) {
            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {//如果取消了勾选
                //就把选项从list中删除
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
                if (object.attributeValue.length == 0) {
                    //如果所有的选择都取消了,就把这个大记录给移除掉
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push(
                //这里push的value值是一个[]数组对象
                {"attributeName": name, "attributeValue": [value]}
            );
        }
    };

    //创建勾选sku列表,即每勾选一个按钮,就动态生成一行对应所选的按钮
    $scope.createItemList = function () {
        //手动创建一个初始化的list集合
        $scope.entity.itemList = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}];
        //初始
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    };

    //添加列的方法
    var addColumn = function (list, columnName, columnValues) {
        var newList = [];//新的集合push对应的attributeName和attributeValue
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆?
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

});	
