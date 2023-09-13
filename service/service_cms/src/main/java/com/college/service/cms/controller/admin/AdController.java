package com.college.service.cms.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.commonutils.result.R;
import com.college.service.cms.entity.Ad;
import com.college.service.cms.entity.vo.AdVo;
import com.college.service.cms.service.AdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 广告推荐 前端控制器
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-29
 */
//@CrossOrigin //解决跨域问题
@Api("推荐位管理 推荐的基本分类")
@RestController
@RequestMapping("/admin/cms/ad")
@Slf4j
public class AdController {

    @Autowired
    private AdService adService;

    @ApiOperation("新增广告推荐")
    @PostMapping("/save")
    public R save(@ApiParam("广告推荐对象")
                  @RequestBody Ad ad) {
        boolean result = adService.save(ad);
        if (result) {
            return R.ok().message("添加广告推荐成功");
        } else {
            return R.error().message("添加失败");
        }
    }

    @ApiOperation("更新广告推荐")
    @PutMapping("/update")
    public R update(@ApiParam("广告推荐对象")
                    @RequestBody Ad ad) {

        boolean result = adService.updateById(ad);
        if (result) {
            return R.ok().message("更新成功");
        } else {
            return R.error().message("更新失败");
        }
    }

    @ApiOperation("根据id获取广告推荐信息")
    @GetMapping("/get/{id}")
    public R getAdById(@ApiParam(value = "广告推荐id", required = true)
                       @PathVariable String id) {
        Ad ad = adService.getById(id);
        if (ad != null) {
            return R.ok().data("item", ad);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("推荐分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R pageList(@ApiParam(value = "当前页码", required = true) @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true) @PathVariable Long limit) {
        Page<AdVo> pageParam = new Page<>(page, limit);
        Page<AdVo> adPage = adService.selectPage(pageParam);
        long total = adPage.getTotal();
        List<AdVo> adVoList = adPage.getRecords();
        return R.ok().data("total", total).data("rows", adVoList);
    }

    @ApiOperation(value = "根据ID删除推荐")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam(value = "推荐ID", required = true) @PathVariable String id) {

        //删除图片
        adService.removeAdImageById(id);

        //删除推荐
        boolean result = adService.removeById(id);
        if (result) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("数据不存在");
        }
    }
}

