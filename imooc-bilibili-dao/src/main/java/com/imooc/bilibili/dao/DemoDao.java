package com.imooc.bilibili.dao;



import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

//mybatis注解 项目启动时自动匹配，将DemoDao文件封装成实体类
// 追踪DemoDao自动跳转到resources中mapper配置的demo.xml中
@Mapper
public interface DemoDao {
    public Map<String,Object> query (Long id);
}
