 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	};
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	};
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
		    $scope.entity.parentId = $scope.parentId;//保存时赋予这个上级id
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	//$scope.reloadList();//重新加载
                    //重新加载就调用查询parentId的方法
                    $scope.findByParentId($scope.parentId);
				}else{
					alert(response.message);
				}
			}		
		);				
	};
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
				    //删除成功后,就刷新当前页面
					//$scope.reloadList();//刷新列表
                    //$scope.findByParentId($scope.parentId);
                    selectList({id:0})
                    //window.reload();
				}						
			}		
		);				
	};
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};

	//上级菜单id,默认查询顶级菜单
    $scope.parentId = 0;
	//根据上级id显示下级列表
    $scope.findByParentId = function (parentId) {
        $scope.parentId = parentId;//记住上级菜单id,保存和修改时需要
        itemCatService.findByParentId(parentId).success(
            function (response) {//List<TbItemCat> json对象
                $scope.list = response;
            }
        )
    };

    //面包屑导航
    //即下一级菜单
    $scope.grade = 1;
    //设置级别
    //每次点击下一级菜单就把这个等级grade+1
    $scope.setGrade = function (value) {
        $scope.grade = value;
    };

    //读取列表
    //每次读取对应的菜单列表都要去判断是哪一级菜单
    $scope.selectList = function (p_entity) {
        if($scope.grade == 1){//如果是一级菜单
            $scope.entity_1 = null;//就只显示顶级菜单
            $scope.entity_2 = null;
        }
        if($scope.grade == 2){//如果是二级菜单
            $scope.entity_1 = p_entity;//二级菜单的值赋给entity_1
            $scope.entity_2 = null;
        }
        if($scope.grade == 3){//如果是三级菜单
            //二级菜单不变
            $scope.entity_2 = p_entity;
        }
        //查询下级菜单
        //通过grader判断查询是哪一级菜单
        $scope.findByParentId(p_entity.id);
    };


    //查询typeId列表
    $scope.typeIdList = {data: []};//品牌列表
    //读取品牌列表
    $scope.selectTypeList = function () {
        //$http.get('../itemCat/selectTypeList.do');
        itemCatService.selectTypeList().success(
            function (response) {// List<Map>
                //返回的是data数据
                $scope.typeIdList = {data: response};
            }
        );
    };
    
});	
