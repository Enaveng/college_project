package com.college.service.acl.controller.admin;


import com.college.commonutils.result.R;
import com.college.service.acl.entity.Permission;
import com.college.service.acl.entity.vo.PermissionVo;
import com.college.service.acl.service.PermissionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 权限 前端控制器
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
@RestController
@RequestMapping("/admin/acl/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;


    //获取全部菜单
    @ApiOperation(value = "查询所有菜单")
    @GetMapping
    public R indexAllPermission() {
        List<Permission> list =  permissionService.queryAllMenu();
        return R.ok().data("children",list);
    }

    @ApiOperation("新增菜单")
    @PostMapping("/save")
    public R save(@RequestBody Permission permission) {
        boolean result = permissionService.save(permission);
        if (result) {
            return R.ok().message("新增菜单成功");
        } else {
            return R.error().message("新增菜单失败");
        }
    }

    @ApiOperation(value = "递归删除菜单")
    @DeleteMapping("/remove/{id}")
    public R remove(@ApiParam(value = "需要删除的菜单id", required = true)
                    @PathVariable String id) {
        permissionService.removeChild(id);
        return R.ok().message("删除成功");
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("/update")
    public R updateById(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return R.ok().message("修改成功");
    }

    @ApiOperation("递归查询嵌套菜单信息")
    @GetMapping("/nested-List")
    public R nestedList() {
        List<PermissionVo> PermissionVoList = permissionService.nestedList();
        return R.ok().data("items", PermissionVoList);
    }

    @ApiOperation(value = "根据角色获取菜单")
    @GetMapping("/toAssign/{roleId}")
    public R toAssign(@PathVariable String roleId) {
        List<Permission> list = permissionService.selectAllMenu(roleId);
        return R.ok().data("children", list);
    }

    @ApiOperation(value = "给角色分配权限")
    @PostMapping("/doAssign")
    public R doAssign(@ApiParam(value = "角色id", required = true) String roleId,
                      @ApiParam(value = "分配的权限菜单id", required = true) String[] permissionId) {
        permissionService.saveRolePermissionRealtionShip(roleId, permissionId);
        return R.ok();
    }


}

