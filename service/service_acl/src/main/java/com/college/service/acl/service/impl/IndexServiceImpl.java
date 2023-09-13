package com.college.service.acl.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.college.service.acl.entity.Role;
import com.college.service.acl.entity.User;
import com.college.service.acl.service.IndexService;
import com.college.service.acl.service.PermissionService;
import com.college.service.acl.service.UserRoleService;
import com.college.service.acl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RedisTemplate redisTemplate;


    //根据用户名获取用户登录信息
    @Override
    public Map<String, Object> getUserInfo(String name) {
        HashMap<String, Object> map = new HashMap<>();
        //根据用户名查询用户对象
        User user = userService.selectByUsername(name);
        //根据用户id查询对应的角色
        Role role = userRoleService.getRoleByUserId(user.getId());

        //根据用户id获取操作权限值
        List<String> permissionValueList = permissionService.selectPermissionValueByUserId(user.getId());
        //向redis中存储数据 键为用户名 值为具体权限值
        redisTemplate.opsForValue().set(name, permissionValueList);

        map.put("name", user.getNickName());
        map.put("avatar", user.getAvatar());
        //项目中每一个用户只对应一个角色
        map.put("roles", role);
        map.put("permissionValueList", permissionValueList);
        return map;
    }

    @Override
    public List<JSONObject> getMenu(String username) {
        User user = userService.selectByUsername(username);

        //根据用户id获取用户菜单权限
        List<JSONObject> permissionList = permissionService.selectPermissionByUserId(user.getId());
        return permissionList;
    }
}
