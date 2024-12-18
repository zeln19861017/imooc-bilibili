package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.UserFollowingDao;
import com.imooc.bilibili.domain.FollowingGroup;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserFollowing;
import com.imooc.bilibili.domain.UserInfo;
import com.imooc.bilibili.domain.constant.UserConstant;
import com.imooc.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// 用户关注相关
@Service
public class UserFollowingService {
    @Autowired
    private UserFollowingDao userFollowingDao;
    @Autowired
    private FollowingGroupService followingGroupService;
    @Autowired
    private UserService userService;

    @Transactional
    // 新增
    public void addUserFollowing(UserFollowing userFollowing){
        // 获取分组信息
        Long groupId = userFollowing.getGroupId();
        if(groupId == null){
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }else{
            FollowingGroup followingGroup = followingGroupService.getByTyId(groupId);
            if(followingGroup == null){
                throw new ConditionException("关注分组不存在!");
            }
        }

        // 查询关注的人是否存在
        Long followingId = userFollowing.getFollowingId();

        User user = userService.getUserById(followingId);
        if(user == null){
            throw new ConditionException("关注的用户不存在！");
        }

        // 删除原来关联分组，再重新增加新的关联关系
        userFollowingDao.deleteUserFolowing(userFollowing.getUserId(),followingId);
        // 关联关系添加
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }


    // 获取关注的用户列表
    // 根据关注的用户的id查询关注的用户基础信息
    // 将关注的用户按关注分组进行分类

    public List<FollowingGroup> getUserFollowings(Long userId){

        // 1.获取关注的用户列表
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        //使用 Java 8 Stream API（推荐）来获取list中的所有followingId
        //结果用set接口来保证没有重复的
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());

        // 2.根据关注的用户的id查询关注的用户基础信息
        List<UserInfo> userInfoList  = new ArrayList<>();
        if(followingIdSet.size() > 0){
             userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        // 遍历关注的人信息，进行匹配
        // 增强型for
        for (UserFollowing userFollowing : list){
            for (UserInfo userInfo : userInfoList){
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setUserinfo(userInfo);
                }
            }
        }

        // 3.按关注分组
        // 获取指定用户id对应的所有关注分组
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);

        // 新建一个全部关注分组
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        // 全部关注分组添加关注的所有用户信息
        allGroup.setFollowingUserInfoList(userInfoList);

        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);

        // 遍历所有关注分组 用id与 用户关注表groupId进行匹配 获取同一组的关注用户信息
        for(FollowingGroup group : groupList){
            List<UserInfo> infoList = new ArrayList<>();
            for(UserFollowing userFollowingTmp: list){
                if(group.getId().equals(userFollowingTmp.getGroupId())){
                    // 同组的用户信息收集
                    infoList.add(userFollowingTmp.getUserinfo());
                }
            }
            group.setFollowingUserInfoList(infoList);

            result.add(group);
        }
        return result;
    }
}
