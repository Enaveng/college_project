package com.college.service.edu.service.impl;

import com.college.service.edu.entity.Comment;
import com.college.service.edu.mapper.CommentMapper;
import com.college.service.edu.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
