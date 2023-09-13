package com.college.service.cms.controller.api;

import com.college.commonutils.result.R;
import com.college.service.cms.controller.admin.AdController;
import com.college.service.cms.entity.Ad;
import com.college.service.cms.service.AdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin //解决跨域问题
@Api("广告推荐")
@RestController
@RequestMapping("/api/cms/ad")
@Slf4j
public class ApiAdController {

    @Autowired
    private AdService adService;

    @ApiOperation("根据推荐位id显示广告推荐")  //id为1代表首页广告位
    @GetMapping("list/{typeId}")
    public R listByAdTypeId(@ApiParam(value = "广告位id", required = true)
                            @PathVariable String typeId) {
        List<Ad> adList = adService.selectListById(typeId);
        return R.ok().data("items", adList);
    }
}
