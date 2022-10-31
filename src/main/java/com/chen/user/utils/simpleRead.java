package com.chen.user.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;

import java.util.List;

/**
 * @author Galaxy
 * @version v1.0
 * @date 2022/7/30
 */
public class simpleRead {
    public static void main(String[] args) {
        String fileName = "D:\\MyDocument\\desktop\\list.xlsx";
        test3(fileName);
    }
    //方法一读取
    public static void test1(String fileName) {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
    }

    public static void test2(String fileName) {
        // 写法2：
        // 匿名内部类 不用额外写一个DemoDataListener
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, DemoData.class, new ReadListener<DemoData>() {

            @Override
            public void invoke(DemoData data, AnalysisContext context) {
                System.out.println(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("读取完成");
            }
        }).sheet().doRead();
    }
        // 写法3
    public static void test3(String fileName) {
        List<Object> list = EasyExcel.read(fileName).head(DemoData.class).sheet().doReadSync();
        System.out.println(list);
    }

}
