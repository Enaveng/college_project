package com.college.service.edu.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.commonutils.result.R;
import com.college.service.edu.entity.Course;
import com.college.service.edu.entity.Teacher;
import com.college.service.edu.entity.form.CourseInfoForm;
import com.college.service.edu.entity.vo.CoursePublishVo;
import com.college.service.edu.entity.vo.CourseQueryVo;
import com.college.service.edu.entity.vo.CourseVo;
import com.college.service.edu.service.CourseService;
import com.college.service.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Api("课程管理")
//@CrossOrigin //解决跨域
@RestController
@RequestMapping("/admin/edu/course")
@Slf4j
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private VideoService videoService;

    @ApiOperation("添加课程信息")
    @PostMapping("/save-course-info")
    public R saveCourse(@ApiParam(value = "课程基本信息", required = true)
                        @RequestBody CourseInfoForm courseInfoForm) {
        //在前端添加课程信息完毕之后 后端需要返回给前端当前课程的id 前端再根据该id添加相应的课程大纲
        String courseId = courseService.saveCourseInfo(courseInfoForm);
        return R.ok().message("添加课程信息成功").data("courseId", courseId);
    }

    @ApiOperation("根据id查询课程信息")
    @GetMapping("/course-info/{id}")
    public R getCourseById(@ApiParam(value = "课程id", required = true)
                           @PathVariable String id) {
        CourseInfoForm courseInfoForm = courseService.getCourseInfoById(id);
        if (courseInfoForm != null) {
            return R.ok().data("item", courseInfoForm);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("更新课程")
    @PutMapping("update-course-info")
    public R getById(@RequestBody CourseInfoForm courseInfoForm) {
        courseService.updateCourseInfoById(courseInfoForm);
        return R.ok().message("修改成功");
    }

    //多表联查(course,teacher,subject) + 分页插件 的使用
    //根据查询条件CourseQueryVo 最终结果得到的是CourseVo
    @ApiOperation("分页查询展示课程列表")
    @GetMapping("list/{page}/{limit}")
    public R coursePage(@ApiParam(value = "当前页码", required = true) @PathVariable long page,
                        @ApiParam(value = "每页记录数", required = true) @PathVariable long limit,
                        @ApiParam(value = "课程列表查询对象") CourseQueryVo courseQueryVo) {
        System.out.println("参数为:" + courseQueryVo);
        Page<CourseVo> coursePage = new Page<>(page, limit);
        Page<CourseVo> pageModel = courseService.selectPage(coursePage, courseQueryVo);
        //得到每页展示的数据
        List<CourseVo> courseList = pageModel.getRecords();
        //得到总的页数
        long total = pageModel.getTotal();
        return R.ok().data("pagedata", courseList).data("pagetotal", total);
    }


    @ApiOperation("根据id删除课程")
    @DeleteMapping("/remove/{id}")
    public R removeById(@ApiParam(value = "课程id", required = true)
                        @PathVariable("id") String id) {
        //删除课程视频
        videoService.removeMediaVideoByCourseId(id);
        //删除课程封面 OSS
        courseService.removeCoverById(id);

        //删除课程相关信息
        boolean result = courseService.removeCourseById(id);
        if (result) {
            return R.ok().message("删除课程信息成功");
        } else {
            return R.error().message("课程信息不存在");
        }
    }

    @ApiOperation("根据id获取课程发布信息")
    @GetMapping("/course-publish/{id}")
    public R getCoursePublishVoById(@ApiParam(value = "发布课程id", required = true)
                                    @PathVariable("id") String id) {
        CoursePublishVo coursePublishVo = courseService.getCoursePublishVoById(id);
        if (coursePublishVo != null) {
            return R.ok().data("item", coursePublishVo);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据id发布课程")
    @PutMapping("/publish-course/{id}")
    public R publishCourseById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String id){

        boolean result = courseService.publishCourseById(id);
        if (result) {
            return R.ok().message("发布成功");
        } else {
            return R.error().message("数据不存在");
        }
    }


}

