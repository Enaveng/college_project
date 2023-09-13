package com.college.service.acl.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.commonutils.result.R;
import com.college.commonutils.util.MD5;
import com.college.service.acl.entity.Role;
import com.college.service.acl.entity.User;
import com.college.service.acl.service.RoleService;
import com.college.service.acl.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
@RestController
@RequestMapping("/admin/acl/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "获取用户分页列表")
    @GetMapping("/{page}/{limit}")
    public R pageList(@ApiParam(value = "当前页码", required = true) @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true) @PathVariable Long limit,
                      @ApiParam(value = "查询对象", required = false) User user) {

        Page<User> pageParam = new Page<>(page, limit);
        IPage<User> pageModel = userService.selectPage(pageParam, user);
        //得到每页展示的数据
        List<User> roleList = pageModel.getRecords();
        //得到总的页数
        long total = pageModel.getTotal();
        return R.ok().data("pagedata", roleList).data("pagetotal", total);
    }

    @ApiOperation(value = "新增管理用户")
    @PostMapping("/save")
    public R save(@RequestBody User user) {
        //使用MD5对密码进行加密操作
        user.setPassword(MD5.encrypt(user.getPassword()));
        userService.save(user);
        return R.ok().message("添加用户成功");
    }

    @ApiOperation(value = "修改管理用户")
    @PutMapping("/update")
    public R updateById(@RequestBody User user) {
        userService.updateById(user);
        return R.ok();
    }

    @ApiOperation(value = "删除管理用户")
    @DeleteMapping("/remove/{id}")
    public R remove(@PathVariable String id) {
        userService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "根据id列表批量删除管理用户")
    @DeleteMapping("/batchRemove")
    public R batchRemove(@RequestBody List<String> idList) {
        userService.removeByIds(idList);
        return R.ok();
    }

    @ApiOperation(value = "根据id获取用户信息")
    @GetMapping("/get/{id}")
    public R getById(@PathVariable String id) {
        User user = userService.getById(id);
        return R.ok().data("item", user);
    }
}

