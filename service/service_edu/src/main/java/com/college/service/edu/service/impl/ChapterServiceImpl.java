package com.college.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.service.edu.entity.Chapter;
import com.college.service.edu.entity.Video;
import com.college.service.edu.entity.vo.ChapterVo;
import com.college.service.edu.entity.vo.VideoVo;
import com.college.service.edu.mapper.ChapterMapper;
import com.college.service.edu.mapper.VideoMapper;
import com.college.service.edu.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.college.service.edu.service.VideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoMapper videoMapper;


    @Override
    public boolean removeChapterById(String id) {

        //根据chapterId删除Video(课时)
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("chapter_id", id);
        videoMapper.delete(videoQueryWrapper);

        //先删除章节
        return this.removeById(id);

    }

    @Override
    public List<ChapterVo> nestedList(String courseId) {
        //方案1：效率低  1+n个sql
        //通过course_id获取章节列表信息：List<Chapter>  sql
        //遍历List<Chapter>{ n
        //    通过chapter_id查询List<Video> sql
        // }

        //方案2：效率高 1+1个sql
        //通过course_id获取章节列表信息：List<Chapter>  sql
        //通过course_id查询List<Video> sql

        //根据course_id获取章节信息列表
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.orderByAsc("sort");
        List<Chapter> chapterList = baseMapper.selectList(queryWrapper);

        //根据course_id获取课时信息列表
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id", courseId);
        videoQueryWrapper.orderByAsc("sort", "id");
        List<Video> videoList = videoMapper.selectList(videoQueryWrapper);

        //组装章节列表：List<ChapterVo>
        List<ChapterVo> chapterVoList = new ArrayList<>();
        for (Chapter chapter : chapterList) {
            //将查询出来的chapter列表遍历
            //拷贝到chapterVo当中
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter, chapterVo);
            //先组装课时列表：List<Video>
            //遍历
            List<VideoVo> videoVoList = new ArrayList<>();
            for (Video video : videoList) {
                VideoVo videoVo = new VideoVo();
                BeanUtils.copyProperties(video, videoVo);
                //判断章节下对应的课时
                if (chapter.getId().equals(video.getChapterId())) {
                    videoVoList.add(videoVo);
                }
            }
            chapterVo.setChildren(videoVoList);
            chapterVoList.add(chapterVo);
        }
        return chapterVoList;
    }
}
