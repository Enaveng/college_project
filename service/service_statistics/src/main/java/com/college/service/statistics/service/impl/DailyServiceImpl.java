package com.college.service.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.commonutils.result.R;
import com.college.service.statistics.entity.Daily;
import com.college.service.statistics.feign.RegisterNumService;
import com.college.service.statistics.mapper.DailyMapper;
import com.college.service.statistics.service.DailyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-10
 */
@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily> implements DailyService {

    @Autowired
    private RegisterNumService registerNumService;

    //存在两次操作数据库 所以需要添加事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createStatisticsByDay(String date) {
        //每次点击生成记录时都需要删除对应日期下存在的数据
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_calculated", date);
        baseMapper.delete(queryWrapper);

        //远程调用微服务得到统计数据
        R r = registerNumService.getRegisterNum(date);
        Map<String, Object> data = r.getData();
        Integer registerNum = (Integer) data.get("registerNum");
        System.out.println(registerNum);
        //仿造数据 使用RandomUtils.nextInt()生成随机数
        int loginNum = RandomUtils.nextInt(100, 200);
        int videoViewNum = RandomUtils.nextInt(100, 200);
        int courseNum = RandomUtils.nextInt(100, 200);

        //创建统计数据对象
        Daily daily = new Daily();
        daily.setRegisterNum(registerNum);
        daily.setLoginNum(loginNum);
        daily.setVideoViewNum(videoViewNum);
        daily.setCourseNum(courseNum);
        daily.setDateCalculated(date);
        baseMapper.insert(daily);
    }

    //组装参数的结果为   {register_num={xData=[1,2,3,4],yData=[5,6,7,8]}} ==> Map<String,Map<String,Object>>
    @Override
    public Map<String, Map<String, Object>> getChartData(String begin, String end) {

        //学员登录数统计
        Map<String, Object> registerNum = this.getChartDataByType(begin, end, "register_num");
        //学员注册数统计
        Map<String, Object> loginNum = this.getChartDataByType(begin, end, "login_num");
        //课程播放数统计
        Map<String, Object> videoViewNum = this.getChartDataByType(begin, end, "video_view_num");
        //每日新增课程数统计
        Map<String, Object> courseNum = this.getChartDataByType(begin, end, "course_num");

        Map<String, Map<String, Object>> map = new HashMap<>();
        map.put("registerNum", registerNum);
        map.put("loginNum", loginNum);
        map.put("videoViewNum", videoViewNum);
        map.put("courseNum", courseNum);

        return map;
    }

    //组装数据的辅助方法 type表示的是要查询的列名  begin,end为前端传递的查询参数
    private Map<String, Object> getChartDataByType(String begin, String end, String type) {

        Map<String, Object> map = new HashMap<>();

        List<String> xList = new ArrayList<>(); //日期列表
        List<Integer> yList = new ArrayList<>(); //数据列表

        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("date_calculated", type);
        queryWrapper.between("date_calculated", begin, end);

        List<Map<String, Object>> mapsData = baseMapper.selectMaps(queryWrapper);
        for (Map<String, Object> data : mapsData) {
            String dateCalculated = (String) data.get("date_calculated");
            xList.add(dateCalculated);

            Integer count = (Integer) data.get(type);
            yList.add(count);
        }

        map.put("xData", xList);
        map.put("yData", yList);
        return map;  //组装的结果为  {xData=[1,2,3,4],yData=[5,6,7,8]}
    }
}
