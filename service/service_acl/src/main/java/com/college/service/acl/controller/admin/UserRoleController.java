package com.college.service.acl.controller.admin;


import com.college.commonutils.result.R;
import com.college.service.acl.service.UserRoleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
@RestController
@RequestMapping("/admin/acl/user-role")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public R toAssign(@PathVariable String userId) {
        Map<String, Object> roleMap = userRoleService.findRoleByUserId(userId);
        return R.ok().data(roleMap);
    }

    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("/doAssign")
    public R doAssign(@ApiParam(value = "用户id", required = true) @RequestParam String userId,
                      @ApiParam(value = "角色id", required = true) @RequestParam String roleId) {
        userRoleService.saveUserRoleRelationship(userId, roleId);
        return R.ok();
    }

    @ApiOperation(value = "根据用户修改角色")
    @PostMapping("/update")
    public R update(@ApiParam(value = "用户id", required = true) @RequestParam String userId,
                    @ApiParam(value = "角色id", required = true) @RequestParam String roleId) {
        userRoleService.updateUserRoleRelationship(userId, roleId);
        return R.ok();
    }

}

