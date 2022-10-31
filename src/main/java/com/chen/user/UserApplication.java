package com.chen.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 项目启动类
 *
 * @author Galaxy
 * @version v1.0
 * @date 2022/5/24
 */
@SpringBootApplication
//定时任务
@EnableScheduling
@Slf4j
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        log.info("localhost:8080/api/doc.html");

    }
}
