package com.zeyigou.shop.controller;

import com.vientu.util.FastDFSClient;
import com.zeyigou.pojo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    //1.读取图片服务器地址
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    //2. 文件上传
    @RequestMapping("upload")
    public Result upload(MultipartFile file){
        try {
            //2.1 得到上传工具类对象
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:properties/fdfs_client.conf");
            //2.2 得到要上传的文件数据
            byte[] bytes = file.getBytes();
            //2.3 进行文件上传，返回的是组名及文件名

            //2.4 处理文件后缀名
            //2.4.1 得到原始文件名
            String filename = file.getOriginalFilename();
            //2.4.2 处理得到文件名
            String suffixName = filename.substring(filename.lastIndexOf(".") + 1);
            //2.4.3 进行文件上传
            String uploadFile = fastDFSClient.uploadFile(bytes, suffixName);

            //2.5 得到最终的文件路径
            String url = FILE_SERVER_URL + uploadFile;
            //2.6 返回
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败！");
        }
    }
}
