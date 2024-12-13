package com.imooc.bilibili.api.support;

import com.imooc.bilibili.domain.exception.ConditionException;
import com.imooc.bilibili.service.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// support包
// 公共的支撑api的基础信息提供
// 配合@componet注解可以构建时依赖注入使用
@Component
public class UserSupport {
    // 根据token获取客户id
    public Long getCurrentUserId(){
        //抓取请求上下文 从请求头中获取token来解密出userId
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = requestAttributes.getRequest().getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
        if(userId<0){
            throw new ConditionException("ID非法");
        }
        return  userId;
    }
}
