package com.college.infrastructure.security.filter;


import com.college.commonutils.result.R;
import com.college.commonutils.util.JwtInfo;
import com.college.commonutils.util.JwtUtils;
import com.college.commonutils.util.ResponseUtil;
import com.college.infrastructure.security.entity.SecurityUser;
import com.college.infrastructure.security.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 登录过滤器，继承UsernamePasswordAuthenticationFilter，对用户名密码进行登录校验
 */

public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * AuthenticationManager是spring security中的认证管理器用来对登录请求进行处理。举个例子讲,
     * 在使用表单登录时处理用户的登录请求的是UsernamePasswordAuthenticationFilter 这个过滤器,
     * 它内部持有一个AuthenticationManager的对象,处理认证登录请求时会调用AuthenticationManager#authenticate方法处理请求。
     */

    private AuthenticationManager authenticationManager;
    private RedisTemplate redisTemplate;

    public TokenLoginFilter(AuthenticationManager authenticationManager, RedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
        this.setPostOnly(false);  //将只接收post请求更改为false
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/acl/login", "POST"));//默认路径
    }

    /**
     * Authentication来封装用户的验证请求信息，Authentication可以是需要验证和已验证的用户请求信息封装
     * （Authentication接口继承Principal接口，Principal接口表示主体的抽象概念，可用于表示任何实体）：
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //使用springboot自动整合的Jackson库将HTTP请求的输入流解析为所需要的User对象
        try {
            //获取Http请求的输入流
            ServletInputStream inputStream = request.getInputStream();
            //创建ObjectMapper对象
            ObjectMapper objectMapper = new ObjectMapper();
            //使用ObjectMapper对象将输入流解析为User对象
            User user = objectMapper.readValue(inputStream, User.class);
            System.out.println(user);
            //创建实现了Authentication接口的UsernamePasswordAuthenticationToken对象 即创建一个未认证的对象
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getNickName(), user.getPassword(), new ArrayList<>());
            //实现认证过程
            return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //将认证成功之后的User对象封装成UserDetails对象
        SecurityUser user = (SecurityUser) authResult.getPrincipal();
        System.out.println("认证成功对象:" + user);
        //生成token
        JwtInfo jwtInfo = new JwtInfo();
        jwtInfo.setAvatar("https://www.bing.com/ck/a?!&&p=444fb841de9735dfJmltdHM9MTY5MjE0NDAwMCZpZ3VpZD0wMDVhOWUzYi1hZmE4LTZmYjItMzZkYi04Y2RlYWU4NjZlM2ImaW5zaWQ9NTUwOA&ptn=3&hsh=3&fclid=005a9e3b-afa8-6fb2-36db-8cdeae866e3b&u=a1L2ltYWdlcy9zZWFyY2g_cT3nlKjmiLfpu5jorqTlpLTlg48mRk9STT1JUUZSQkEmaWQ9OTc5OUMzNUREQTlGQkY4Qjg5ODRGMjEwREFCOTBFRkRDMUJBQjc0RQ&ntb=1");
        jwtInfo.setNickname(user.getCurrentUserInfo().getNickName());
        jwtInfo.setId(UUID.randomUUID().toString());
        String jwtToken = JwtUtils.getJwtToken(jwtInfo, 1800);
        System.out.println("获取用户名:===="+user.getCurrentUserInfo().getNickName() );
        System.out.println("获取用户名:===="+user.getUsername());
        System.out.println("值==============="+user.getPermissionValueList());
        //将用户名当作键，用户对应的权限当作值写入redis 30分钟自动过期
        redisTemplate.opsForValue().set(user.getCurrentUserInfo().getNickName(), user.getPermissionValueList());
        //前端返回
        ResponseUtil.out(response, R.ok().data("token", jwtToken));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ResponseUtil.out(response, R.error());
    }
}
