package com.college.service.ucenter.mapper;

import com.college.service.ucenter.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-31
 */
public interface MemberMapper extends BaseMapper<Member> {

    Integer selectRegisterNumByDay(String date);
}
