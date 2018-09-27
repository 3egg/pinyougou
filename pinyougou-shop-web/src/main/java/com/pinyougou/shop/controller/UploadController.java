package com.pinyougou.shop.controller;

import com.pinyougou.common.util.FastDFSClient;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    //上传文件的方法
    @RequestMapping("/upload")
    public Result uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //把最后一个点去掉,得到扩展名
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        try {
            //读取配置文件,创建一个fastDFS客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");
            //读取字节流,加上扩展名
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //返回数据库需要存储的地址
            String url = "http://192.168.25.133/" + path;
            return new Result(true, url);//通过message返回交给img标签中的src属性
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "upload failed");
        }
    }
}
