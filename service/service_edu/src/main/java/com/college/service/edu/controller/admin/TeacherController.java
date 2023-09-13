package com.college.service.edu.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.R;
import com.college.commonutils.util.ExceptionUtils;
import com.college.service.edu.entity.Teacher;
import com.college.service.edu.entity.vo.TeacherQueryVo;
import com.college.service.edu.feign.OssFileService;
import com.college.service.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
//将后台管理接口与前台分开
//@CrossOrigin //允许跨域请求
@Api("讲师管理")
@RestController
@Slf4j
@RequestMapping("/admin/edu/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private OssFileService ossFileService;

    //获取所有的讲师信息
    @GetMapping("/findAll")
    @ApiOperation("获取所有讲师信息")
    public R getAll() {
        List<Teacher> teacherList = teacherService.list();
        return R.ok().data("items", teacherList);

    }

    //根据id删除讲师信息

    /**
     * 注:，@TableLogic注解是逻辑删除，并不会永久删除数据，实体类加上这个注解再执行删除方法的时候会变成修改
     * 使用removeById时会将删除方法变成修改方法  并不会真实删除数据库数据 对应的是字段 isDeleted
     */
    @ApiOperation("根据id删除讲师信息")
    @DeleteMapping("/remove/{id}")
    public R removeById(@ApiParam("讲师id") @PathVariable("id") String id) {
        //删除讲师头像
        teacherService.removeAvatarById(id);

        //删除讲师
        boolean b = teacherService.removeById(id);
        if (b) {
            return R.ok().message("删除讲师信息成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    //使用实体类接收请求参数 是指请求参数通过url地址拼接进行传递
    @ApiOperation("讲师分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码", required = true) @PathVariable("page") Long page,
                      @ApiParam(value = "每页记录数", required = true) @PathVariable("limit") Long limit,
                      @ApiParam("课程列表查询对象") TeacherQueryVo teacherQueryVo) {
        System.out.println("参数为:" + teacherQueryVo);
        //page为当前分页页码 limit为每页展示的记录数
        Page<Teacher> objectPage = new Page<>(page, limit);
        Page<Teacher> teacherPage = teacherService.selectPage(objectPage, teacherQueryVo);
        //得到每页展示的数据
        List<Teacher> teacherList = teacherPage.getRecords();
        //得到总的页数
        long total = teacherPage.getTotal();
        return R.ok().data("pagedata", teacherList).data("pagetotal", total);
    }

    @ApiOperation("添加讲师信息")
    @PostMapping("/save")
    public R save(@ApiParam("讲师对象") @RequestBody Teacher teacher) {
        try {
            boolean b = teacherService.save(teacher);
            return R.ok().message("添加讲师成功");
        } catch (Exception e) {
            //该添加讲师如果添加的name属性已经存在的话会报错 所以对该异常进行捕获
            log.error(ExceptionUtils.getMessage(e));
            throw new CollegeException("添加讲师失败", 20002);
        }
    }

    @ApiOperation("修改讲师信息")
    @PutMapping("/update")
    public R update(@ApiParam("讲师对象") @RequestBody Teacher teacher) {
        boolean result = teacherService.updateById(teacher);
        if (result) {
            return R.ok().message("修改成功");
        } else {
            return R.error().message("数据不存在,修改失败");
        }
    }

    @ApiOperation("根据id获取讲师信息")
    @GetMapping("/get/{id}")
    public R getById(@ApiParam("讲师id") @PathVariable String id) {
        Teacher teacher = teacherService.getById(id);
        if (teacher != null) {
            return R.ok().data("item", teacher);
        } else {
            return R.error().message("数据不存在");
        }
    }

    //批量删除
    @ApiOperation("根据id列表删除讲师信息")
    @DeleteMapping("/batchRemove")
    public R removeById(@ApiParam("讲师id列表")
                        //前端传递的是json字符串所以需要规定泛型为String
                        @RequestBody List<String> idlist) {
        boolean b = teacherService.removeByIds(idlist);
        if (b) {
            return R.ok().message("删除讲师信息成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    //根据用户输入的关键字进行查询
    @ApiOperation("根据关键字查询讲师名列表")
    @GetMapping("/list/name/{key}")
    public R selectNameListByKey(@ApiParam(value = "输入关键字", required = true)
                                 @PathVariable("key") String key) {
        List<Map<String, Object>> nameList = teacherService.selectNameListByKey(key);
        return R.ok().data("nameList", nameList);
    }


    @ApiOperation("测试Feign的使用")
    @GetMapping("/test")
    public R testFeign() {
        ossFileService.test();
        return R.ok();
    }
}