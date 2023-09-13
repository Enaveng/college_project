package com.college.service.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.service.acl.entity.Role;
import com.college.service.acl.mapper.RoleMapper;
import com.college.service.acl.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Page<Role> selectPage(Page<Role> objectPage, Role role) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        String roleName = role.getRoleName();
        if (!StringUtils.isEmpty(roleName)) {
            //向条件构造器中添加条件
            queryWrapper.like("role_name", roleName);
        }
        return baseMapper.selectPage(objectPage, queryWrapper);
    }

}
