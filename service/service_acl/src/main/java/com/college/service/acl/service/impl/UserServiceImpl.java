package com.college.service.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.service.acl.entity.User;
import com.college.service.acl.mapper.UserMapper;
import com.college.service.acl.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public IPage<User> selectPage(Page<User> pageParam, User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        String nickName = user.getNickName();
        if (!StringUtils.isEmpty(nickName)) {
            queryWrapper.like("nick_name", nickName);
        }
        return baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public User selectByUsername(String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nick_name",name);
        User user = baseMapper.selectOne(queryWrapper);
        return user;
    }
}
