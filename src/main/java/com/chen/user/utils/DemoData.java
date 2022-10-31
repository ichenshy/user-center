package com.chen.user.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DemoData {
    @ExcelProperty("编号")
    private Long id;
    @ExcelProperty("姓名")
    private String userName;
}
