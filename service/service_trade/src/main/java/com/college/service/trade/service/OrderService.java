package com.college.service.trade.service;

import com.college.commonutils.util.JwtInfo;
import com.college.service.trade.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-05
 */
public interface OrderService extends IService<Order> {

    String saveOrder(String courseId, JwtInfo jwtInfo);

    Order getByOrderId(String orderId, String memberId);

    Boolean isBuyByCourseId(String courseId, String memberId);

    List<Order> selectByMemberId(String memberId);

    Boolean removeByMemberId(String orderId, String memberId);
}
