package com.ningmeng.manage_cms_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by wangb on 2020/2/11.
 */
@SpringBootApplication
@EntityScan("com.ningmeng.framework.domain.cms")//扫描实体类

@ComponentScan(basePackages={"com.ningmeng.manage_cms_client"})//扫描本项目下的所有类
@ComponentScan(basePackages={"com.ningmeng.framework"})//扫描异常下的包
public class ManageCmsApplication {
    public static void main(String[] args){
        SpringApplication.run(ManageCmsApplication.class,args);
    }
}
