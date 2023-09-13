package com.college.service.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.baseservice.dto.CourseDto;
import com.college.baseservice.dto.MemberDto;
import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.ResultCodeEnum;
import com.college.commonutils.util.JwtInfo;
import com.college.service.trade.entity.Order;
import com.college.service.trade.feign.EduCourseService;
import com.college.service.trade.feign.UcenterMemberService;
import com.college.service.trade.mapper.OrderMapper;
import com.college.service.trade.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.college.service.trade.util.OrderNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-05
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private EduCourseService eduCourseService;
    @Autowired
    private UcenterMemberService ucenterMemberService;


    @Override
    public String saveOrder(String courseId, JwtInfo jwtInfo) {
        //查询当前课程该用户是否已经创建订单
        String memberId = jwtInfo.getId();
        //根据用户id 和 课程id进行查询
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("member_id", memberId);
        Order orderReady = baseMapper.selectOne(queryWrapper);
        if (orderReady != null) { //表示订单已经创建 直接返回订单id
            return orderReady.getId();
        }
        //组装订单信息并创建  远程调用查询接口
        CourseDto courseDto = eduCourseService.getCourseDtoById(courseId);
        if (courseDto == null) {
            throw new CollegeException(ResultCodeEnum.PARAM_ERROR);
        }
        MemberDto memberDto = ucenterMemberService.getMemberDtoByMemberId(memberId);
        if (memberDto == null) {
            throw new CollegeException(ResultCodeEnum.PARAM_ERROR);
        }

        //创建订单
        Order order = new Order();
        order.setOrderNo(OrderNoUtils.getOrderNo()); //订单号

        order.setCourseId(courseId);
        order.setCourseTitle(courseDto.getTitle());
        order.setCourseCover(courseDto.getCover());
        order.setTeacherName(courseDto.getTeacherName());
        order.setTotalFee(courseDto.getPrice().multiply(new BigDecimal(100)));//单位：分

        order.setMemberId(memberId);
        order.setMobile(memberDto.getMobile());
        order.setNickname(memberDto.getNickname());

        order.setStatus(0);//未支付
        order.setPayType(1); //微信支付

        baseMapper.insert(order);
        return order.getId();
    }

    @Override
    public Order getByOrderId(String orderId, String memberId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", orderId).eq("member_id", memberId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean isBuyByCourseId(String courseId, String memberId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId).eq("member_id", memberId).eq("status", 1);
        Integer count = baseMapper.selectCount(queryWrapper);
        //intValue()将此 Integer 的值作为 int 返回。
        return count.intValue() > 0;
    }

    @Override
    public List<Order> selectByMemberId(String memberId) {
        //查询用户的订单信息
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId)
                .orderByDesc("gmt_create");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Boolean removeByMemberId(String orderId, String memberId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", orderId).eq("member_id", memberId);
        //使用OrderService接口的删除方法返回值是boolean类型
        return this.remove(queryWrapper);
    }
}
