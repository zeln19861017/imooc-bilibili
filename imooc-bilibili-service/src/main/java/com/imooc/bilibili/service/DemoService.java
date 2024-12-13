package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

// 启动时把DemoService作为一个需要发布的bean添加到上下文中
@Service
public class DemoService {
    // 依赖相关注入
    @Autowired
    private DemoDao demoDao;
    public Map<String,Object> query (Long id){
        return demoDao.query(id);
    }
}
