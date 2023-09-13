package com.college.service.statistics.controller.admin;


import com.college.commonutils.result.R;
import com.college.service.statistics.service.DailyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-10
 */
@Api("统计分析管理")
@RestController
@RequestMapping("/admin/statistics/daily")
public class DailyController {
    @Autowired
    private DailyService dailyService;

    @ApiOperation("生成统计记录")
    @PostMapping("/create/{date}")
    public R createdStatisticsByDay(@ApiParam(value = "统计日期", required = true)
                                    @PathVariable String date) {
        dailyService.createStatisticsByDay(date);
        return R.ok().message("数据生成成功");
    }


    @ApiOperation("显示统计数据")
    @GetMapping("show-chart/{begin}/{end}")
    public R showChart(
            @ApiParam("开始时间") @PathVariable String begin,
            @ApiParam("结束时间") @PathVariable String end) {

        Map<String, Map<String, Object>> map = dailyService.getChartData(begin, end);
        return R.ok().data("chartData", map);
    }

}

