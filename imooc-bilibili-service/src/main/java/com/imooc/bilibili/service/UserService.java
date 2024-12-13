package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.UserDao;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserInfo;
import com.imooc.bilibili.domain.constant.UserConstant;
import com.imooc.bilibili.domain.exception.ConditionException;
import com.imooc.bilibili.service.util.MD5Util;
import com.imooc.bilibili.service.util.RSAUtil;
import com.imooc.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public void addUser(User user) {
        // 手机号判断
        String phone= user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("phone不能为空");
        }

        User dbUser = this.getUserByPhone(phone);
        if(dbUser != null){
            throw new ConditionException("phone已注册！");
        }
        // 插入数据准备
        Date now = new Date();
        //用户密码md5加密 时间戳作为盐
        //前端传入数据是RSA加密过的，需要先解密，再加盐md5加密
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword(); // 前端传入的RSA加密的密码
        String rawPassword;
        try{
            rawPassword = RSAUtil.decrypt(password); // RSA解密
        }catch(Exception e){
            throw new ConditionException("密码解密失败！");
        }
        String md5Password = MD5Util.sign(rawPassword,salt,"UTF-8");

        // 插入数据
        user.setPhone(phone);
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);
        //添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        // 新建一个相关的系统固定的包用来存放一些常量
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
    }

    public User getUserByPhone(String phone){
        return userDao.getUserByPhone(phone);
    }

    // 手机号密码登录验证
    public String login(User user) throws Exception {
        // 手机号判断
        String phone= user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("phone不能为空");
        }

        //查询手机号
        User dbUser = this.getUserByPhone(phone);
        if(dbUser == null){
            throw new ConditionException("当前用户不存在！");
        }

        // 验证密码
        // 前端传入的密码与用户数据中的盐生成签名与用户数据中的密码进行比较
        String password = user.getPassword(); // 前端传入的RSA加密的密码
        String rawPassword;
        try{
            rawPassword = RSAUtil.decrypt(password); // RSA解密
        }catch(Exception e){
            throw new ConditionException("密码解密失败！");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword,salt,"UTF-8");
        if(! md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误");
        }
        //验证成功生成用户令牌token信息返回
        TokenUtil tokenUtil = new TokenUtil();
        return  tokenUtil.generateToken(dbUser.getId());
    }

    public User getUserInfo(Long userId){
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        // 整合
        return user;
    }
}
