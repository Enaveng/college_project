package com.college.service.edu.controller.api;

import com.college.commonutils.result.R;
import com.college.commonutils.util.JwtInfo;
import com.college.commonutils.util.JwtUtils;
import com.college.service.edu.entity.vo.CourseCollectVo;
import com.college.service.edu.service.CourseCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api("收藏课程管理")
@RestController
@RequestMapping("/api/edu/course-collect")
//@CrossOrigin
@Slf4j
public class ApiCollectCourseController {

    @Autowired
    private CourseCollectService courseCollectService;

    @ApiOperation(value = "添加收藏课程")
    @PostMapping("auth/save/{courseId}")
    public R saveCourseCollect(@PathVariable String courseId, HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        //得到登录的用户id
        String memberId = jwtInfo.getId();
        courseCollectService.saveCollect(courseId, memberId);
        return R.ok().message("收藏课程成功");
    }

    @ApiOperation(value = "判断该课程是否已被用户收藏")
    @GetMapping("/auth/is-collect/{courseId}")
    public R isCollect(@ApiParam(name = "courseId", value = "课程id", required = true)
                       @PathVariable String courseId,
                       HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        //得到登录的用户id
        String memberId = jwtInfo.getId();
        boolean isCollect = courseCollectService.isCollect(courseId, memberId);
        return R.ok().data("isCollect", isCollect);
    }

    @ApiOperation(value = "获取课程收藏列表")
    @GetMapping("/auth/list")
    public R collectList(HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        List<CourseCollectVo> list = courseCollectService.selectListByMemberId(jwtInfo.getId());
        return R.ok().data("items", list);
    }

    @ApiOperation(value = "取消收藏课程")
    @DeleteMapping("auth/remove/{courseId}")
    public R remove(
            @ApiParam(name = "courseId", value = "课程id", required = true)
            @PathVariable String courseId,
            HttpServletRequest request) {

        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        boolean result = courseCollectService.removeCourseCollect(courseId, jwtInfo.getId());
        if (result) {
            return R.ok().message("已取消");
        } else {
            return R.error().message("取消失败");
        }
    }
}
