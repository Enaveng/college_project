package com.college.service.edu.controller.admin;


import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.R;
import com.college.commonutils.result.ResultCodeEnum;
import com.college.commonutils.util.ExceptionUtils;
import com.college.service.edu.entity.vo.SubjectVo;
import com.college.service.edu.service.SubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Api("导入Excel")
//@CrossOrigin //解决跨域
@RestController
@RequestMapping("admin/edu/subject")
@Slf4j
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @ApiOperation("Excel批量导入课程")
    @PostMapping("/import")
    public R batchImport(
            @ApiParam(value = "Excel文件", required = true)
            @RequestPart("file") MultipartFile file) {

        try {
            InputStream inputStream = file.getInputStream();
            subjectService.batchImport(inputStream);
            return R.ok().message("批量导入成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new CollegeException(ResultCodeEnum.EXCEL_DATA_IMPORT_ERROR);
        }
    }

    @ApiOperation("嵌套数据列表")
    @GetMapping("/nested-list")
    public R nestedList() {
        List<SubjectVo> subjectVoList = subjectService.nestedList();
        return R.ok().data("items", subjectVoList);
    }
}

