package com.college.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.commonutils.result.R;
import com.college.service.edu.entity.Course;
import com.college.service.edu.entity.Teacher;
import com.college.service.edu.entity.vo.TeacherQueryVo;
import com.college.service.edu.feign.OssFileService;
import com.college.service.edu.mapper.CourseMapper;
import com.college.service.edu.mapper.TeacherMapper;
import com.college.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private OssFileService ossFileService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Page<Teacher> selectPage(Page<Teacher> objectPage, TeacherQueryVo teacherQueryVo) {
        //定义条件构造器
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        // 1、排序：按照sort字段升序排序
        queryWrapper.orderByAsc("sort");
        // 2、当没有选择查询的条件时 按照正常的分页查询
        if (teacherQueryVo == null) {
            return baseMapper.selectPage(objectPage, queryWrapper);
        }
        // 3、根据所选条件进行查询
        String name = teacherQueryVo.getName();
        Integer level = teacherQueryVo.getLevel();
        String joinDateBegin = teacherQueryVo.getJoinDateBegin();
        String joinDateEnd = teacherQueryVo.getJoinDateEnd();
        /**
         * 注：在下列代码中不可以使用isEmpty()  这是因为isEmpty()方法是字符串类的方法，
         *    只能用于字符串对象 判断空对象时会抛出NullPointerException异常
         */
        if (!StringUtils.isEmpty(name)) {
            //进行模糊查询
            queryWrapper.like("name", name);
        }

        if (level != null) {
            queryWrapper.eq("level", level);
        }

        if (!StringUtils.isEmpty(joinDateBegin)) {
            queryWrapper.ge("join_date", joinDateBegin);
        }

        if (!StringUtils.isEmpty(joinDateEnd)) {
            queryWrapper.le("join_date", joinDateEnd);
        }
        return baseMapper.selectPage(objectPage, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> selectNameListByKey(String key) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", key);
        //表示只查询name字段
        queryWrapper.select("name");
        List<Map<String, Object>> selectMaps = baseMapper.selectMaps(queryWrapper);
        return selectMaps;
    }

    @Override
    public boolean removeAvatarById(String id) {
        //首先根据id到数据库中查询对应的avatar字段
        Teacher teacher = baseMapper.selectById(id);
        //判断teacher是否存在
        if (teacher != null) {
            String avatar = teacher.getAvatar();
            //判断该用户是否上传了头像
            if (!StringUtils.isEmpty(avatar)) {
                R r = ossFileService.remove(avatar);
                return r.getSuccess();
            }
        }
        return false;
    }

    @Override
    public boolean removeAvatarByIds(List<String> ids) {
        return false;
    }

    //向前台网站展示的信息
    @Override
    public Map<String, Object> selectTeacherInfoById(String id) {
        //根据讲师id查询讲师信息以及对应的课程
        Teacher teacher = baseMapper.selectById(id);
        QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.eq("teacher_id", id);
        List<Course> courseList = courseMapper.selectList(courseQueryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("teacher", teacher);
        map.put("course", courseList);
        return map;
    }

    @Override
    public List<Teacher> selectHotTeacher() {
        //先从redis中查询数据 如果redis中存在数据则直接返回
        //定义key
        String redisKey = "college:index:teacher";
        List<Teacher> teacherList = (List<Teacher>) redisTemplate.opsForValue().get(redisKey);
        //判断缓存中是否存在数据
        if (teacherList!=null){
            return teacherList;  //缓存中存在数据
        }
        //无缓存 从数据库中查询
        //根据sort查询前四个讲师信息
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sort");
        queryWrapper.last("limit 4");
        teacherList = baseMapper.selectList(queryWrapper);
        //写缓存
        redisTemplate.opsForValue().set(redisKey,teacherList,30000, TimeUnit.MILLISECONDS);
        return teacherList;
    }
}
