package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
// 关联mybatis
public interface UserDao {
    User getUserByPhone(String phone);
    User getUserByPhoneOrEmail(String phone, String email);
    User getUserByPhoneOrEmailRaw(String phoneOrEmail);
    Integer addUser(User user);
    void addUserInfo(UserInfo userInfo);
    User getUserById(Long id);
    UserInfo getUserInfoByUserId(Long id);
}
