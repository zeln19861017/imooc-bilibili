package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

// 添加springboot注解
@SpringBootApplication(scanBasePackages = "com.imooc.bilibili")
public class ImoocBilibiliApp {
    public static void main(String[] args) {
//        启动的环境在service层properties中配置
        ApplicationContext app = SpringApplication.run(ImoocBilibiliApp.class, args);

    }
}
