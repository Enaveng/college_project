package com.college.service.trade.feign.fallback;


import com.college.baseservice.dto.CourseDto;
import com.college.commonutils.result.R;
import com.college.service.trade.feign.EduCourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EduCourseServiceFallback implements EduCourseService {

    @Override
    public CourseDto getCourseDtoById(String courseId) {
        log.info("熔断保护");
        return null;
    }
}
