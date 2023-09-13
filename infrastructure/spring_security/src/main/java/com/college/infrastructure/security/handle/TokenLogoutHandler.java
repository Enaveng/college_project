package com.college.infrastructure.security.handle;

import com.college.commonutils.result.R;
import com.college.commonutils.util.JwtInfo;
import com.college.commonutils.util.JwtUtils;
import com.college.commonutils.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 登出业务逻辑类
 * </p>
 */
public class TokenLogoutHandler implements LogoutHandler {


    private RedisTemplate redisTemplate;

    public TokenLogoutHandler(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //得到请求头中的token
        String token = request.getHeader("token");
        if (token != null) {
            JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
            //得到用户数据
            String nickname = jwtInfo.getNickname();
            redisTemplate.delete(nickname);
        }
        ResponseUtil.out(response, R.ok());
    }
}