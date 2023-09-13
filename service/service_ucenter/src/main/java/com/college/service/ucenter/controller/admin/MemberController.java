package com.college.service.ucenter.controller.admin;

import com.college.commonutils.result.R;
import com.college.service.ucenter.entity.Member;
import com.college.service.ucenter.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("统计用户注册管理")
@RestController
@RequestMapping("/admin/ucenter/member")
@Slf4j
public class MemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation("统计用户注册的人数")
    @GetMapping("/count-register-num/{date}")
    public R getRegisterNum(@ApiParam(value = "查询的日期", required = true)
                            @PathVariable String date) {
        Integer count = memberService.countRegisterNum(date);
        return R.ok().data("registerNum", count);
    }
}
