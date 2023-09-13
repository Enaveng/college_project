package com.college.service.edu.controller.api;


import com.college.commonutils.result.R;
import com.college.service.edu.entity.Course;
import com.college.service.edu.entity.Teacher;
import com.college.service.edu.service.CourseService;
import com.college.service.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@CrossOrigin
@Api("首页展示数据")
@RestController
@RequestMapping("/api/edu/index")
public class ApiIndexController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherService teacherService;

    @ApiOperation("课程和讲师的首页热门数据")
    @GetMapping
    public R index() {

        //查询热门课程
        List<Course> courseHotList = courseService.selectHotCourse();

        //查询推荐讲师
        List<Teacher> teacherHotList = teacherService.selectHotTeacher();

        return R.ok().data("courseHotList", courseHotList).data("teacherHotList", teacherHotList);
    }

}

