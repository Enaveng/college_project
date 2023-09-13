package com.college.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.service.edu.entity.Chapter;
import com.college.service.edu.entity.Video;
import com.college.service.edu.feign.VodVideoService;
import com.college.service.edu.mapper.VideoMapper;
import com.college.service.edu.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private VodVideoService vodVideoService;

    @Override
    public void removeVideoBySourceId(String id) {
        //先根据id查找到对应的课时信息
        Video video = baseMapper.selectById(id);
        //得到视频字段
        String videoSourceId = video.getVideoSourceId();
        //远程调用删除视频
        vodVideoService.removeVideo(videoSourceId);
    }

    //实现删除chapter时删除其下的所有视频
    @Override
    public void removeVideoByChapterId(String id) {
        //先根据chapter_id查询出对应的video
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("video_source_id");
        queryWrapper.eq("chapter_id", id);
        List<Map<String, Object>> videoList = baseMapper.selectMaps(queryWrapper);
        List<String> videoIdList = this.createIdList(videoList);
        vodVideoService.removeVideoByIdList(videoIdList);
    }


    @Override
    public void removeMediaVideoByCourseId(String id) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("video_source_id");
        queryWrapper.eq("course_id", id);

        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        List<String> videoSourceIdList = this.createIdList(maps);
        vodVideoService.removeVideoByIdList(videoSourceIdList);
    }


    //实现组装id集合的方法
    public List<String> createIdList(List<Map<String, Object>> videoList) {
        List<String> videoIdList = new ArrayList<>();
        for (Map<String, Object> map : videoList) {
            String sourceId = (String) map.get("video_source_id");
            videoIdList.add(sourceId);
        }
        return videoIdList;
    }
}
