package com.college.service.acl.mapper;

import com.college.service.acl.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.college.service.acl.entity.vo.PermissionVo;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * <p>
 * 权限 Mapper 接口
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    List<String> selectAllPermissionValue();

    List<String> selectPermissionValueByUserId(String userId);

    List<Permission> selectPermissionByUserId(String userId);
}
