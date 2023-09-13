package com.college.infrastructure.security.config;


import com.college.infrastructure.security.filter.TokenAuthenticationFilter;
import com.college.infrastructure.security.filter.TokenLoginFilter;
import com.college.infrastructure.security.handle.DefaultPasswordEncoder;
import com.college.infrastructure.security.handle.TokenLogoutHandler;
import com.college.infrastructure.security.handle.UnauthorizedEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * <p>
 * Security配置类
 * </p>
 *
 * @author qy
 * @since 2019-11-18
 */
@Configuration
@EnableWebSecurity  //该注解可选 只添加@Configuration注解也可以自定义配置类
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DefaultPasswordEncoder defaultPasswordEncoder;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * configure(HttpSecurity http): 这个方法用于配置HttpSecurity对象，
     * 它定义了哪些URL路径需要被保护，哪些不需要被保护，以及如何进行身份验证和授权。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(new UnauthorizedEntryPoint())  //无权限访问调用UnauthorizedEntryPoint()处理类
                .and().csrf().disable()     //关闭csrf功能:跨站请求伪造,默认只能通过post方式提交logout请求
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().logout().logoutUrl("/admin/acl/index/logout") //退出路径
                .addLogoutHandler(new TokenLogoutHandler(redisTemplate)).and()  //退出执行的类TokenLogoutHandler
                //添加自定义过滤器
                .addFilter(new TokenLoginFilter(authenticationManager(), redisTemplate))
                //.httpBasic()启用基本的 HTTP 身份验证（httpBasic）
                .addFilter(new TokenAuthenticationFilter(authenticationManager(), redisTemplate)).httpBasic();
    }


    /**
     * configure(AuthenticationManagerBuilder auth): 这个方法用于配置AuthenticationManagerBuilder对象，
     * 它定义了如何进行身份验证。你可以通过调用auth对象的方法来指定用户的身份验证方式 ，
     * 比如基于内存的验证、数据库验证或LDAP验证。
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }

    /**
     * 指定哪些静态资源或特定的URL路径不需要经过 Spring Security 的过滤器链进行安全验证   配置哪些请求不拦截
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/**",
                "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**"
        );
    }
}