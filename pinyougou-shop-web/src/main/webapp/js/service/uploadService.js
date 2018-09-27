app.service('uploadService', function ($http) {
    //这个上传文件的方法
    this.uploadFile = function () {
        //手动给他创建了一个form表单
        var formData = new FormData();
        //然后往表单里面添加数据
        //第一个参数：file和服务端中的controller中的参数即(MultipartFile file)一致
        //第二个参数：file 和input 中的id一致 这里支持多图片，现在只要一个所以就选一张即可.
        formData.append('file', file.files[0]);
        return $http({//向controller返回一个相当于ajax
            method: 'post',
            url: '../upload.do',
            data: formData,//提交的数据就是自定义的form表单里面的数据
            //这样浏览器会帮我们把Content-Type 设置为 multipart/form-data.
            //浏览器默认为 application/json
            headers: {'Content-type': undefined},
            transformRequest: angular.identity
        });
    }
});