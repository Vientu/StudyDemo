package com.vientu.test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.*;

/**
 * FreemarkerDemo
 *
 * @Author Vientu
 * @Date 2020/9/24 15:10
 */

public class FreemarkerDemo {
    public static void main(String[] args) throws IOException, TemplateException {
        //1.得到模板的配置对象
        Configuration configuration = new Configuration();

        //2.设置配置对象的参数
        //2.1 设置模板所在文件位置
        configuration.setDirectoryForTemplateLoading(new File("D:\\demo\\springDemo\\zeyigou_parent\\freemarkerDome\\src\\main\\resources"));
        //2.2 设置模板的字符集编码
        configuration.setDefaultEncoding("UTF-8");

        //3.通过模板对象得到模板
        Template template = configuration.getTemplate("test.ftl");

        //4.执行模板处理器
        //4.1 定义数据模型
        Map dataModel = new HashMap<>();
        dataModel.put("name","张三");
        dataModel.put("message","欢迎学习freemarker模板技术!");

        //4.2 定义一组List<Map>对象
        Map map1 = new HashMap();
        map1.put("id",1001);
        map1.put("fname","苹果");
        map1.put("price",10);
        map1.put("num",5);
        Map map2 = new HashMap();
        map2.put("id",1002);
        map2.put("fname","橘子");
        map2.put("price",8);
        map2.put("num",6);
        Map map3 = new HashMap();
        map3.put("id",1003);
        map3.put("fname","猕猴桃");
        map3.put("price",13);
        map3.put("num",8);

        List<Map> mapList = new ArrayList<>();
        mapList.add(map1);
        mapList.add(map2);
        mapList.add(map3);

        dataModel.put("list",mapList);

        //4.3 定义一个日期变量
        dataModel.put("today",new Date());

        //4.4 定义一个数字类型
        dataModel.put("point",12345678);

        //定义模板输出流对象
        Writer out = new FileWriter("e:\\item\\test.html");
        //执行模板
        template.process(dataModel,out);
        //关闭流
        out.close();
    }
}
