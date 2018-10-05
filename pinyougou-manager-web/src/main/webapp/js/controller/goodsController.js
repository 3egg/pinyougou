//控制层
app.controller('goodsController', function ($scope, $location,$controller, itemCatService, goodsService,typeTemplateService) {

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
    /*$scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    };*/
    //给页面上传图片的实体
    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};
    //查询实体/回显数据
    $scope.findOne = function () {
        var id = $location.search()['id'];//获取参数值
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                //回显图片
                $scope.entity.goodsDesc.itemImages = angular.fromJson($scope.entity.goodsDesc.itemImages);
                //回显扩展属性
                $scope.entity.goodsDesc.customAttributeItems = angular.fromJson($scope.entity.goodsDesc.customAttributeItems);
                //规格
                $scope.entity.goodsDesc.specificationItems = angular.fromJson($scope.entity.goodsDesc.specificationItems);
                //sku列表规格转换
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    //遍历sku规格详情
                    $scope.entity.itemList[i].spec = angular.fromJson($scope.entity.itemList[i].spec);
                }
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
                    $scope.reloadList();//重新加载
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

    // 0 未审核 1已审核 3... 4..
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];
    $scope.itemCatList = [];//商品分类列表
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                //[ {id: 1, name: "图书、音像、电子书刊", parentId: 0, typeId: 35},{..}]
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        )
    };

    //更改状态
    $scope.updateStatus = function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(
            function (response) {//Result
                if(response.success){
                    $scope.selectIds = [];//清空ids集合
                    $scope.reloadList();//刷新列表
                }else{
                    alert(response.message);
                }
            }
        )
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
        if (newValue != undefined) {//这里页面初始化时做一个判断
            //根据选择的值,查询二级分类
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat2List = response;
                    $scope.itemCat3List = null;
                }
            );
        }
    });

    //读取三级菜单分类
    //监听到了二级菜单的值发生了变化就改变三级菜单的值
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        if (newValue != undefined) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat3List = response;
                }
            );
        }
    });

    //读取模板id , 三级菜单一动就读取模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldVlaue) {
        if (newValue != undefined) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.goods.typeTemplateId = response.typeId;
                }
            );
        }
    });

    //模板id有值了就根据模板id找到品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        if (newValue != undefined) {
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    //获取模板类型,得到里面额brand_id数据
                    //typeTemplate数据格式为
                    //{id:35,name:xxx,specIds:[{id:27,text:xx},{..}],brandIds:[{id:1,text:xx},{..}],c_a_i:[{text:xx},{..}]}
                    $scope.typeTemplate = response;
                    //获取到了品牌列表
                    $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                    //读取模板信息时,也要获取到扩展属性
                    //页面展示的是自定义goods 里面包含了goodsDesc,goods和List<TbItem>数据
                    //这里就把typeTemplate里面的扩展属性数据转换成json,
                    //然后页面展示就用的是goods.goodsDesc.customAttribute扩展属性
                    //[{"text":"内存大小"},{"text":"颜色"}]
                    if ($location.search()['id'] == null) {//如果#?id=1有值
                        $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                    }
                }
            );

            //模板即typeTemplate有值了,就查询规格列表,这里是用于页面显示模板的规格
            typeTemplateService.findSpecList(newValue).success(
                function (response) {//List<Map>
                    $scope.specList = response;
                }
            );
        }
    });

    //这是用动态把页面的显示内容,准备好数据提交给服务端
    //这里的entity的goodsDesc,specificationItems数据先设置为null
    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};
    //最终向服务端传递的数据格式为List<TbItem> [{'spec':{},'price':0,'num':999,'status':'0','isDefault':'0'}];
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
        } else {//第一次没有就添加对象
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
        var items = $scope.entity.goodsDesc.specificationItems;//{"attributeName": name, "attributeValue": [value]}
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
            //这里的数据就变为了[{"spec":{"网络":"移动3G","机身内存":"16G"},"price":0,"num":9999,"status":"0","isDefault":"0"}];
        }
    };

    //添加列的方法/深克隆
    var addColumn = function (list, columnName, columnValues) {
        var newList = [];//新的集合push对应的attributeName和attributeValue
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                //创建一个新的对象 , 里面的值一样 ,但是内存地址不一样
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    };

    //根据规格名称和选项返回是否被勾选
    $scope.checkAttributeValue = function (specName, optionName) {
        //把商品规格这个对象数据拿出来
        var items = $scope.entity.goodsDesc.specificationItems;
        //判断specName和attributeName是否有可以返回一个object对象
        var object = $scope.searchObjectByKey(items, 'attributeName', specName);
        if (object == null) {
            return false;
        } else {
            //有对象且attributeValue有值就单选框checked=true
            if (object.attributeValue.indexOf(optionName) >= 0) {
                return true;
            } else {
                return false;
            }
        }
    }

});	
