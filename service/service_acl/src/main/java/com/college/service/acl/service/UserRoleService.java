package com.college.service.acl.service;

import com.college.service.acl.entity.Role;
import com.college.service.acl.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
public interface UserRoleService extends IService<UserRole> {

    void saveUserRoleRelationship(String userId, String roleId);

    void updateUserRoleRelationship(String userId, String roleId);

    Map<String, Object> findRoleByUserId(String userId);

    Role getRoleByUserId(String userId);
}
