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
import java.util.List;

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
        userInfo.setUserId(user.getId());
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
    public User getUserByPhoneOrEmail(String phoneOrEmail){
        return userDao.getUserByPhoneOrEmailRaw(phoneOrEmail);
    }
    public List<User> getUserByPhoneOrEmailMany(String phoneOrEmail){
        return (List<User>) userDao.getUserByPhoneOrEmailRawMany(phoneOrEmail);
    }

    public User getUserByPhoneOrEmail(String phone,String email){
        return userDao.getUserByPhoneOrEmail(phone, email);
    }

    // 手机号密码登录验证
    public String login(User user) throws Exception {
        // 手机号判断
        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();
        if (StringUtils.isNullOrEmpty(phone) &&  StringUtils.isNullOrEmpty(email)) {
            throw new ConditionException("phone/email不能为空");
        }

        //查询
        String phoneOrEmail = phone + email; // 字符串拼接
//        User dbUser = this.getUserByPhoneOrEmail(phoneOrEmail);

        List<User> dbUserList = this.getUserByPhoneOrEmailMany(phoneOrEmail);
        User dbUser;
        if (dbUserList.isEmpty()) {
            throw new ConditionException("当前用户不存在！");
        } else if (dbUserList.size() > 1) {
            throw new ConditionException("查询结果不唯一！");
        } else {
            dbUser = dbUserList.get(0);
        }

        if (dbUser == null) {
            throw new ConditionException("当前用户不存在！");
        }

        // 验证密码
        // 前端传入的密码与用户数据中的盐生成签名与用户数据中的密码进行比较
        String password = user.getPassword(); // 前端传入的RSA加密的密码
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password); // RSA解密
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误");
        }
        //验证成功生成用户令牌token信息返回
        TokenUtil tokenUtil = new TokenUtil();
        return tokenUtil.generateToken(dbUser.getId());
    }

    public User getUserInfo(Long userId){
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        // 整合
        return user;
    }
    // 更新
    public void updateUsers(User user) throws Exception {
        Long id = user.getId();
        User dbUser = userDao.getUserById(id);
        if(dbUser == null){
            throw new ConditionException("用户不存在");
        }

        if(!StringUtils.isNullOrEmpty(user.getPassword())){
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        user.setUpdateTime(new Date());
        userDao.updateUsers(user);
    }
    // 更新
    public void updateUserInfos(UserInfo userInfo) throws Exception {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);
    }

    public User getUserById(Long followingId) {
        return userDao.getUserById(followingId);
    }

}
