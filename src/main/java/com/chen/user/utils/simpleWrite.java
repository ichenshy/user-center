package com.chen.user.utils;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Galaxy
 * @version v1.0
 * @date 2022/7/30
 */
public class simpleWrite {
    public static void main(String[] args) {
        String fileName = "D:\\MyDocument\\desktop\\list.xlsx";
        test1(fileName);
    }

    private static void test1(String fileName) {
        // 写法1 JDK8+
        EasyExcel.write(fileName, DemoData.class)
                .sheet("模板").doWrite(()->{
                    return getList();
                });
                //.doWrite(() -> {
                //    // 分页查询数据
                //    return getList();
                //}
                //);
    }

    private static void test2(String fileName) {
        // 写法2
        EasyExcel.write(fileName, DemoData.class).sheet("模板").doWrite(getList());
    }

    private static List getList() {
        ArrayList<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DemoData data = new DemoData();
            data.setId((long) i);
            data.setUserName("姓名" + i);
            list.add(data);
        }
        return list;
    }
}
