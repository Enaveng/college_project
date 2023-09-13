package com.college.service.acl.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.commonutils.result.R;
import com.college.service.acl.entity.Role;
import com.college.service.acl.entity.User;
import com.college.service.acl.service.RoleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
@RestController
@RequestMapping("/admin/acl/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "新增角色")
    @PostMapping("/save")
    public R save(@ApiParam("角色对象")
                  @RequestBody Role role) {
        roleService.save(role);
        return R.ok().message("添加成功");
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("/remove/{id}")
    public R remove(@ApiParam(value = "角色id", required = true)
                    @PathVariable String id) {
        roleService.removeById(id);
        return R.ok().message("删除成功");
    }

    @ApiOperation(value = "修改角色")
    @PutMapping("/update")
    public R updateById(@RequestBody Role role) {
        roleService.updateById(role);
        return R.ok().message("修改成功");
    }

    @ApiOperation(value = "根据id列表批量删除角色")
    @DeleteMapping("/batchRemove")
    public R batchRemove(@RequestBody List<String> idList) {
        roleService.removeByIds(idList);
        return R.ok().message("批量删除成功");
    }

    @ApiOperation(value = "分页查询角色数据")
    @GetMapping("/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码", required = true) @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true) @PathVariable Long limit,
                      @ApiParam(value = "条件查询实体类") Role role) {
        Page<Role> objectPage = new Page<>(page, limit);
        Page<Role> rolePage = roleService.selectPage(objectPage, role);
        //得到每页展示的数据
        List<Role> roleList = rolePage.getRecords();
        //得到总的页数
        long total = rolePage.getTotal();
        return R.ok().data("pagedata", roleList).data("pagetotal", total);
    }

    @ApiOperation(value = "根据id获取角色信息")
    @GetMapping("/get/{id}")
    public R getById(@PathVariable String id) {
        Role role = roleService.getById(id);
        return R.ok().data("item", role);
    }

}

