package com.college.service.ucenter.service;

import com.college.baseservice.dto.MemberDto;
import com.college.service.ucenter.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.college.service.ucenter.entity.vo.LoginVo;
import com.college.service.ucenter.entity.vo.RegisterVo;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-31
 */
public interface MemberService extends IService<Member> {

    void register(RegisterVo registerVo);

    String login(LoginVo loginVo);

    Member getMemberByOpenid(String openid);

    MemberDto getMemberDtoByMemberId(String memberId);

    Integer countRegisterNum(String date);
}
