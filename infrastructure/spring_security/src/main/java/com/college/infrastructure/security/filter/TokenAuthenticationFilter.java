package com.college.infrastructure.security.filter;

import com.college.commonutils.result.R;
import com.college.commonutils.util.JwtInfo;
import com.college.commonutils.util.JwtUtils;
import com.college.commonutils.util.ResponseUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 访问过滤器
 * </p>
 */
public class TokenAuthenticationFilter extends BasicAuthenticationFilter {

    //必须使用构造器注入
    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager, RedisTemplate redisTemplate) {
        super(authenticationManager);
        this.redisTemplate = redisTemplate;
    }

    //前端的每一个请求都会经过这个方法
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("=================" + request.getRequestURI());
        if (request.getRequestURI().indexOf("admin") == -1) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = null;
        try {
            authentication = getAuthentication(request);
        } catch (Exception e) {
            ResponseUtil.out(response, R.error());
        }
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            ResponseUtil.out(response, R.error());
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        //通过请求体获取token
        String jwtToken = request.getHeader("token");
        System.out.println("得到的请求头是= ------ " + jwtToken);
        //传递request请求调用Jwt工具类得到用户名
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        String nickname = jwtInfo.getNickname();
        System.out.println("取到的名字================" + nickname);
        //根据用户名在redis中获取对应的权限列表
        List<String> permissionValueList = (List<String>) redisTemplate.opsForValue().get(nickname);
        //接收的权限列表是 Collection<? extends GrantedAuthority> authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        //遍历从redis中获取的权限列表
        for (String permissionValue : permissionValueList) {
            //创建GrantedAuthority接口的实现类
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(permissionValue);
            authorities.add(simpleGrantedAuthority);
        }
        if (!StringUtils.isEmpty(nickname)) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(nickname, jwtToken, authorities);
            //返回的是存有权限信息的对象
            return authenticationToken;
        }
        return null;
    }
}
