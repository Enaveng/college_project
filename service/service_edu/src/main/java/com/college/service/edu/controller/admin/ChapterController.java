package com.college.service.edu.controller.admin;

import com.college.commonutils.result.R;
import com.college.service.edu.entity.Chapter;
import com.college.service.edu.entity.vo.ChapterVo;
import com.college.service.edu.service.ChapterService;
import com.college.service.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("章节管理")
@RestController
//@CrossOrigin
@RequestMapping("/admin/edu/chapter")
@Slf4j
public class ChapterController {
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private VideoService videoService;

    @ApiOperation("添加章节信息")
    @PostMapping("/save")
    public R save(@ApiParam(value = "章节信息对象", required = true)
                  @RequestBody Chapter chapter) {
        boolean result = chapterService.save(chapter);
        if (result) {
            return R.ok().message("保存章节信息成功");
        } else {
            return R.error().message("保存失败");
        }
    }

    @ApiOperation("根据id查询章节信息")
    @GetMapping("/get/{id}")
    public R getChapterById(@ApiParam(value = "课程id", required = true)
                            @PathVariable("id") String id) {
        Chapter chapter = chapterService.getById(id);
        if (chapter != null) {
            return R.ok().data("item", chapter);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据id修改章节信息")
    @PutMapping("/update")
    public R updateById(@ApiParam(value = "章节对象", required = true)
                        @RequestBody Chapter chapter) {
        boolean result = chapterService.updateById(chapter);
        if (result) {
            return R.ok().message("修改章节信息成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据ID删除课程")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam(value = "课程id", required = true) @PathVariable String id) {

        //此处调用vod中的删除视频文件的接口
        videoService.removeVideoByChapterId(id);

        //删除课程  删除课程包括删除章节信息、云端视频、数据库video信息
        boolean result = chapterService.removeChapterById(id);
        if (result) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("嵌套章节数据列表")
    @GetMapping("nested-list/{courseId}")
    public R nestedListByCourseId(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String courseId) {

        List<ChapterVo> chapterVoList = chapterService.nestedList(courseId);
        return R.ok().data("items", chapterVoList);
    }

}
