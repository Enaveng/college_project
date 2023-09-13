package com.college.service.acl.service;

import com.alibaba.fastjson.JSONObject;
import com.college.service.acl.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.college.service.acl.entity.vo.PermissionVo;

import java.util.List;

/**
 * <p>
 * 权限 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
public interface PermissionService extends IService<Permission> {

    //获取全部菜单
    List<Permission> queryAllMenu();

    //根据角色获取菜单
    List<Permission> selectAllMenu(String roleId);

    //递归删除菜单
    void removeChildById(String id);

    List<PermissionVo> nestedList();

    void removeChild(String id);

    void saveRolePermissionRealtionShip(String roleId, String[] permissionId);

    List<String> selectPermissionValueByUserId(String userId);

    List<JSONObject> selectPermissionByUserId(String userId);
}
