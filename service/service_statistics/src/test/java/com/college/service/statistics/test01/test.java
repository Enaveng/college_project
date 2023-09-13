package com.college.service.statistics.test01;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.service.statistics.entity.Daily;
import com.college.service.statistics.mapper.DailyMapper;
import com.college.service.statistics.service.DailyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class test {

    @Autowired
    private DailyService dailyService;

    @Test
    public void test01() {

        Map<String, Object> map1 = new HashMap<>();

        List<String> xList = new ArrayList<>(); //日期列表
        List<Integer> yList = new ArrayList<>(); //数据列表

        //通过时间段的查询 组装出x轴上的日期 y轴上的具体数据
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("date_calculated", "register_num")
                .between("date_calculated", "2018-12-28", "2019-01-04");
        List<Map<String, Object>> maps = dailyService.listMaps(queryWrapper);
        for (Map<String, Object> map : maps) {
            Integer registerNum = (Integer) map.get("register_num");
            yList.add(registerNum);
            String date = (String) map.get("date_calculated");
            xList.add(date);
        }
        System.out.println(yList+"/////"+xList);
        map1.put("xData", xList);
        map1.put("yData", yList);
        System.out.println("1111"+map1);
    }
}
//{111={xData=[11],yData=[21213]}}