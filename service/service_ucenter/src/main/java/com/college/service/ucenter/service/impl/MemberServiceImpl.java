package com.college.service.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.baseservice.dto.MemberDto;
import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.ResultCodeEnum;
import com.college.commonutils.util.FormUtils;
import com.college.commonutils.util.JwtInfo;
import com.college.commonutils.util.JwtUtils;
import com.college.commonutils.util.MD5;
import com.college.service.ucenter.entity.Member;
import com.college.service.ucenter.entity.vo.LoginVo;
import com.college.service.ucenter.entity.vo.RegisterVo;
import com.college.service.ucenter.mapper.MemberMapper;
import com.college.service.ucenter.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-31
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void register(RegisterVo registerVo) {
        //校验参数
        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();

        if (StringUtils.isEmpty(mobile)
                || !FormUtils.isMobile(mobile)) {
            throw new CollegeException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }

        if (StringUtils.isEmpty(nickname)
                || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(code)) {
            throw new CollegeException(ResultCodeEnum.PARAM_ERROR);
        }

        //校验验证码：redis
        String checkCode = (String) redisTemplate.opsForValue().get(mobile);
        if (!code.equals(checkCode)) {
            throw new CollegeException(ResultCodeEnum.CODE_ERROR);
        }

        //判断用户是否已经被注册 查询手机号
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        Integer count = baseMapper.selectCount(queryWrapper);
        //判断查询出来的记录数count是否大于0
        if (count > 0) {
            throw new CollegeException(ResultCodeEnum.REGISTER_MOBLE_ERROR);
        }

        //注册
        Member member = new Member();
        member.setNickname(nickname);
        member.setMobile(mobile);
        //使用MD5工具类对密码进行加密然后保存到数据库中
        member.setPassword(MD5.encrypt(password));
        member.setDisabled(false);
        member.setAvatar("https://img.zcool.cn/community/01cfd95d145660a8012051cdb52093.png@1280w_1l_2o_100sh.png");
        baseMapper.insert(member);
    }

    @Override
    public String login(LoginVo loginVo) {
        //判断用户输入的手机号是否合法 输入的密码是否为空
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        if (StringUtils.isEmpty(mobile) || !FormUtils.isMobile(mobile) || StringUtils.isEmpty(password)) {
            throw new CollegeException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }
        //根据手机号进行查询
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        //查询得到对应的用户对象
        Member member = baseMapper.selectOne(queryWrapper);
        //判断是否存在对应的用户
        if (member == null) {
            throw new CollegeException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }
        //校验密码
        if (!MD5.encrypt(password).equals(member.getPassword())) {
            throw new CollegeException(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        //校验用户是否被禁用
        if (member.getDisabled()) {
            throw new CollegeException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        //登录成功 得到JwtToken
        JwtInfo info = new JwtInfo();
        info.setId(member.getId());
        info.setNickname(member.getNickname());
        info.setAvatar(member.getAvatar());
        //expire单位为 s 表示过期时间为半个小时
        String jwtToken = JwtUtils.getJwtToken(info, 1800);
        return jwtToken;
    }

    @Override
    public Member getMemberByOpenid(String openid) {
        //根据openid查找用户信息
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        Member member = baseMapper.selectOne(queryWrapper);
        return member;
    }

    @Override
    public MemberDto getMemberDtoByMemberId(String memberId) {
        Member member = baseMapper.selectById(memberId);
        MemberDto memberDto = new MemberDto();
        BeanUtils.copyProperties(member, memberDto);
        return memberDto;
    }

    @Override
    public Integer countRegisterNum(String date) {
        return baseMapper.selectRegisterNumByDay(date);
    }
}
