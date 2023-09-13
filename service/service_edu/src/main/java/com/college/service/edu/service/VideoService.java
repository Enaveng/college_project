package com.college.service.edu.service;

import com.college.service.edu.entity.Video;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
public interface VideoService extends IService<Video> {
   void removeVideoBySourceId(String Id);

   void removeVideoByChapterId(String id);

   void removeMediaVideoByCourseId(String id);
}
