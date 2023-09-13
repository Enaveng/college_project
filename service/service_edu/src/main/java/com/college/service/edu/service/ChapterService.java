package com.college.service.edu.service;

import com.college.service.edu.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.college.service.edu.entity.vo.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
public interface ChapterService extends IService<Chapter> {

    boolean removeChapterById(String id);

    List<ChapterVo> nestedList(String courseId);
}
