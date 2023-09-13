package com.college.service.acl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.service.acl.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
public interface RoleService extends IService<Role> {

    Page<Role> selectPage(Page<Role> objectPage, Role role);

}
