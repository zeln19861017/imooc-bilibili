package com.imooc.bilibili.api;

import com.imooc.bilibili.api.support.UserSupport;
import com.imooc.bilibili.domain.JsonResponse;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.service.UserService;
import com.imooc.bilibili.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;

// UserApi 引入UserService, UserService 引入UserDao
// API 接口层/控制层 调转到业务实现逻辑Service层，业务实现逻辑中需要用到数据库交互，service访问dao，dao通过mybatis产生关联与数据库交互,交互结果返回service
@RestController
public class UserApi {
    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    // 使用token获取用户信息
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo(){
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return  new JsonResponse<>(user);
    }


    // 获取公钥
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getResPublicKey() throws Exception {
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

    // 新建用户
    // 参数：接收前端封装的json类型的User实体类
    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user){
        userService.addUser(user);
        return JsonResponse.success();
    }

    // 登录
    // jwt返回用户令牌
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }


}
