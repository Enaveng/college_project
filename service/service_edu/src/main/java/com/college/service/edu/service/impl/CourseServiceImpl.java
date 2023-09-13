package com.college.service.edu.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.baseservice.dto.CourseDto;
import com.college.commonutils.result.R;
import com.college.service.edu.entity.*;
import com.college.service.edu.entity.form.CourseInfoForm;
import com.college.service.edu.entity.vo.*;
import com.college.service.edu.feign.OssFileService;
import com.college.service.edu.mapper.*;
import com.college.service.edu.service.CourseDescriptionService;
import com.college.service.edu.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseDescriptionMapper courseDescriptionMapper;
    @Autowired
    private OssFileService ossFileService;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CourseCollectMapper courseCollectMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public String saveCourseInfo(CourseInfoForm courseInfoForm) {
        //数据库采用纵向发表策略 需要分别向两个表中插入数据

        //1.保存course
        Course course = new Course();
        //将前端传递的courseInfoForm对应赋值到course当中
        BeanUtils.copyProperties(courseInfoForm, course);
        //给Status设置默认值
        course.setStatus(Course.COURSE_DRAFT);
        //保存数据
        baseMapper.insert(course);
        String id = course.getId();

        //2.保存courseDescription
        CourseDescription courseDescription = new CourseDescription();
        //赋值
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(id);
        courseDescriptionMapper.insert(courseDescription);
        //将id返回给前端
        return id;
    }

    @Override
    public CourseInfoForm getCourseInfoById(String id) {
        Course course = baseMapper.selectById(id);
        if (course == null) {
            return null;
        }
        //根据id获取CourseDescription
        CourseDescription courseDescription = courseDescriptionMapper.selectById(id);
        String description = courseDescription.getDescription();
        //组装成CourseInfoForm
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(course, courseInfoForm);
        courseInfoForm.setDescription(description);
        return courseInfoForm;
    }

    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfoForm) {

        //更新Course
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        baseMapper.updateById(course);

        //更新CourseDescription
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescriptionMapper.updateById(courseDescription);
    }

    @Override
    public Page<CourseVo> selectPage(Page<CourseVo> coursePage, CourseQueryVo courseQueryVo) {
        //组装查询条件
        QueryWrapper<CourseVo> queryWrapper = new QueryWrapper<>();
        //根据创建时间进行降序
        queryWrapper.orderByDesc("c.gmt_create");
        String subjectId = courseQueryVo.getSubjectId();
        String teacherId = courseQueryVo.getTeacherId();
        String title = courseQueryVo.getTitle();
        String subjectParentId = courseQueryVo.getSubjectParentId();
        //多表联查时需要指定哪个数据库中的字段
        if (!StringUtils.isEmpty(subjectId)) {
            queryWrapper.eq("c.subject_id", subjectId);
        }
        if (!StringUtils.isEmpty(teacherId)) {
            queryWrapper.eq("c.teacher_id", teacherId);
        }
        if (!StringUtils.isEmpty(title)) {
            queryWrapper.eq("c.title", title);
        }
        if (!StringUtils.isEmpty(subjectParentId)) {
            queryWrapper.eq("c.subject_parent_id", subjectParentId);
        }
        //组装分页
        //执行查询
        //只需要在mapper层传入封装好的分页对象即可 mp会自动将分页查询语句组装到sql当中
        List<CourseVo> courseVoList = baseMapper.selectPageByCourseQueryVo(coursePage, queryWrapper);
        //将查询结果设置到分页当中  在多表查询条件下不可以使用mp自带的分页查询 因此不会将查询结果组装到分页当中
        return coursePage.setRecords(courseVoList);
    }

    @Override
    public boolean removeCoverById(String id) {
        //首先根据id到数据库中查询对应的Course字段
        Course course = baseMapper.selectById(id);
        //判断Course是否存在
        if (course != null) {
            String cover = course.getCover();
            //判断该课程是否上传了封面
            if (!StringUtils.isEmpty(cover)) {
                R r = ossFileService.remove(cover);
                return r.getSuccess();
            }
        }
        return false;
    }

    /**
     * 数据库中外键约束的设置：
     * 互联网分布式项目中不允许使用外键与级联更新，一切设计级联的操作不要依赖数据库层，要在业务层解决
     * <p>
     * 如果业务层解决级联删除功能
     * 那么先删除子表数据，再删除父表数据
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCourseById(String id) {

        //根据courseId删除Video(课时)
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id", id);
        videoMapper.delete(videoQueryWrapper);

        //根据courseId删除Chapter(章节)
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id", id);
        chapterMapper.delete(chapterQueryWrapper);

        //根据courseId删除Comment(评论)
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("course_id", id);
        commentMapper.delete(commentQueryWrapper);

        //根据courseId删除CourseCollect(课程收藏)
        QueryWrapper<CourseCollect> courseCollectQueryWrapper = new QueryWrapper<>();
        courseCollectQueryWrapper.eq("course_id", id);
        courseCollectMapper.delete(courseCollectQueryWrapper);

        //根据courseId删除CourseDescription(课程详情)
        courseDescriptionMapper.deleteById(id);

        //删除课程
        //this关键字可以调用当前对象的方法以及属性
        return this.removeById(id);
    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        return baseMapper.selectCoursePublishVoById(id);
    }

    @Override
    public boolean publishCourseById(String id) {
        //根据id修改Status字段
        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        return this.updateById(course);
    }

    @Override
    public List<Course> webGetCourseList(WebCourseQueryVo webCourseQueryVo) {
        //根据前台查询条件进行查询
        //得到的结果只能是已经发布的课程
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();

        //查询已发布的课程
        queryWrapper.eq("status", Course.COURSE_NORMAL);

        if (!StringUtils.isEmpty(webCourseQueryVo.getSubjectParentId())) {
            queryWrapper.eq("subject_parent_id", webCourseQueryVo.getSubjectParentId());
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getSubjectId())) {
            queryWrapper.eq("subject_id", webCourseQueryVo.getSubjectId());
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getBuyCountSort())) {
            queryWrapper.orderByDesc("buy_count");
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getGmtCreateSort())) {
            queryWrapper.orderByDesc("gmt_create");
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getPriceSort())) {
            if (webCourseQueryVo.getType() == null || webCourseQueryVo.getType() == 1) {
                queryWrapper.orderByAsc("price");
            } else {
                queryWrapper.orderByDesc("price");
            }
        }

        return baseMapper.selectList(queryWrapper);
    }


    /**
     * 获取课程信息并更新浏览量
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public WebCourseVo selectWebCourseVoById(String id) {
        Course course = baseMapper.selectById(id);
        //更新浏览数
        course.setViewCount(course.getViewCount() + 1);
        baseMapper.updateById(course);
        //获取课程信息
        return baseMapper.selectWebCourseVoById(id);
    }

    //使用redis缓存优化查询速度    在实际业务也可以规定一个定时业务来进行缓存预热(适应场景: 数据更新不大,用户访问量多)
    //避免缓存击穿可以使用 双检加锁策略
    @Override
    public List<Course> selectHotCourse() {
        //先从redis中查询数据 如果redis中存在数据则直接返回
        //定义key
        String redisKey = "college:index:course";
        List<Course> courseList = (List<Course>) redisTemplate.opsForValue().get(redisKey);
        //判断缓存中是否存在数据
        if (courseList != null) {
            return courseList;  //缓存中存在数据
        }
        //无缓存 从数据库中查询
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        //查询前八个热门课程
        //根据浏览数进行倒序
        queryWrapper.orderByDesc("view_count");
        //last用户手动将sql语句拼接到最后
        queryWrapper.last("limit 8");
        courseList = baseMapper.selectList(queryWrapper);
        //写缓存 必须要指定过期时间 此处指定30s
        redisTemplate.opsForValue().set(redisKey, courseList, 30000, TimeUnit.MILLISECONDS);
        return courseList;
    }

    @Override
    public CourseDto getCourseDtoById(String courseId) {

        return baseMapper.selectCourseDtoById(courseId);

    }

}
