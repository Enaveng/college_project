package com.college.service.statistics.task;


import com.college.service.statistics.service.DailyService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author helen
 * @since 2020/5/8
 */
@Slf4j
@Component
public class ScheduledTask {

    @Autowired
    private DailyService dailyService;

    @Scheduled(cron = "0 0 1 * * ?")   //表示每天的凌晨一点自动执行数据统计任务
    // @Scheduled(cron = "30 47 16 * * ?")
    public void testGenStatisticsData() {
        log.info("testGenStatisticsData 在执行......");
        String day = new DateTime().minusDays(1).toString("yyyy-MM-dd");
        dailyService.createStatisticsByDay(day);
    }
}
