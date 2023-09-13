package com.college.service.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.service.acl.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
public interface UserService extends IService<User> {

    IPage<User> selectPage(Page<User> pageParam, User user);

    User selectByUsername(String name);
}
