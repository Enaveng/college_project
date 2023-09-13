package com.college.service.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.service.acl.entity.Role;
import com.college.service.acl.entity.UserRole;
import com.college.service.acl.mapper.RoleMapper;
import com.college.service.acl.mapper.UserRoleMapper;
import com.college.service.acl.service.RoleService;
import com.college.service.acl.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;

    @Override
    public void saveUserRoleRelationship(String userId, String roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        baseMapper.insert(userRole);
    }

    @Override
    public void updateUserRoleRelationship(String userId, String roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        baseMapper.updateById(userRole);
    }

    @Override
    public Map<String, Object> findRoleByUserId(String userId) {
        //查询所有的角色
        List<Role> allRolesList = roleService.list(null);

        //根据用户id，查询用户拥有的角色id
        List<UserRole> existUserRoleList = this.list(new QueryWrapper<UserRole>().eq("user_id", userId).select("role_id"));

        List<String> existRoleList = existUserRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());

        //对角色进行分类
        List<Role> assignRoles = new ArrayList<Role>();
        for (Role role : allRolesList) {
            //已分配
            if (existRoleList.contains(role.getId())) {
                assignRoles.add(role);
            }
        }

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoles", assignRoles);
        roleMap.put("allRolesList", allRolesList);
        return roleMap;
    }

    @Override
    public Role getRoleByUserId(String userId) {
        System.out.println(userId);
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).select("role_id");
        UserRole userRole = userRoleService.getOne(queryWrapper);
        System.out.println(userRole);
        //获取角色id
        String roleId = userRole.getRoleId();
        Role role = roleService.getById(roleId);
        System.out.println("查询到对应的角色为"+role);
        return role;
    }
}
