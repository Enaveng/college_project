package com.college.service.statistics.service;

import com.college.service.statistics.entity.Daily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-10
 */
public interface DailyService extends IService<Daily> {

    void createStatisticsByDay(String date);

   Map<String,Map<String, Object>> getChartData(String begin, String end);
}
