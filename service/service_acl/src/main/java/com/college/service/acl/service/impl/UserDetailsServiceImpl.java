package com.college.service.acl.service.impl;


import com.college.infrastructure.security.entity.SecurityUser;
import com.college.service.acl.entity.User;
import com.college.service.acl.service.PermissionService;
import com.college.service.acl.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

//自定义查询数据库中的用户信息 将这些信息封装为 UserDetails 对象返回给 Spring Security 进行身份验证和授权。
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;


    /***
     * 根据账号获取用户信息
     * @param username:
     * @return: org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中取出用户信息
        User user = userService.selectByUsername(username);
        System.out.println("user" + user);

        // 判断用户是否存在
            if (null == user) {
            //throw new UsernameNotFoundException("用户名不存在！");
        }
        // 返回UserDetails实现类
        com.college.infrastructure.security.entity.User curUser = new com.college.infrastructure.security.entity.User();
        BeanUtils.copyProperties(user, curUser);

        //selectPermissionValueByUserId(user.getId())主要是根据用户的id查询具体的权限值类型
        List<String> authorities = permissionService.selectPermissionValueByUserId(user.getId());
        System.out.println("==================" + authorities);
        SecurityUser securityUser = new SecurityUser(curUser);
        securityUser.setPermissionValueList(authorities);
        return securityUser;
    }
}

