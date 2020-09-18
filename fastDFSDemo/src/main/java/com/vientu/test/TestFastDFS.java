package com.vientu.test;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class TestFastDFS {
    public static void main(String[] args) throws Exception {
        //1. 初始化fastDFS环境
        ClientGlobal.init("D:\\demo\\springDemo\\zeyigou_parent\\fastDFSDemo\\src\\main\\resources\\fdfs_client.conf");
        //2.构造一个trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //3. 获得一个trackerServer服务端
        TrackerServer connection = trackerClient.getConnection();
        //4. 构造一个storageClient
        StorageClient storageClient = new StorageClient(connection, null);
        //5.进行文件上传
        String[] fileInfo = storageClient.upload_file("D:\\demo\\springDemo\\zeyigou_parent\\zeyigou_manager_web\\src\\main\\webapp\\img\\banner2.jpg","jpg",null);
        //6. 遍历数组
        for (String s : fileInfo) {
            System.out.println(s);
        }
    }
}
