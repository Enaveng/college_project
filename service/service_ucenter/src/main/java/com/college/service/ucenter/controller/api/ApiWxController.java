package com.college.service.ucenter.controller.api;

import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.R;
import com.college.commonutils.result.ResultCodeEnum;
import com.college.commonutils.util.ExceptionUtils;
import com.college.commonutils.util.HttpClientUtils;
import com.college.commonutils.util.JwtInfo;
import com.college.commonutils.util.JwtUtils;
import com.college.service.ucenter.entity.Member;
import com.college.service.ucenter.service.MemberService;
import com.college.service.ucenter.util.UcenterProperties;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
//@CrossOrigin
@Slf4j
@RequestMapping("/api/ucenter/wx")
public class ApiWxController {

    @Autowired
    private UcenterProperties ucenterProperties;
    @Autowired
    private MemberService memberService;

    /**
     * 请求获取微信登录二维码  可以通过在PC端打开以下链接：
     * https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=回调地址
     * &response_type=code&scope=snsapi_login&state=随机数#wechat_redirect
     */
    @ApiOperation("跳转页面获取微信登录二维码")
    @GetMapping("/login")
    public String genQrConnect(HttpSession session) {
        //组装url地址
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //需要对redirectUri进行特殊字符转换 将://转换为 %3A%2F%2F
        String redirectUri = null;
        try {
            redirectUri = URLEncoder.encode(ucenterProperties.getRedirectUri(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CollegeException(ResultCodeEnum.URL_ENCODE_ERROR);
        }

        //使用UUID生成随机数state 该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
        String state = UUID.randomUUID().toString();
        //将state保存到session当中 当微信请求我们自定义的接口时 我们的服务端需要对其请求进行校验 所以将state保存到session当中
        //session是保存在服务端当中  分布式情况下，session会失效  使用Spring-Session 集成好的解决方案，将Session存放在Redis中进行共享
        //原理: 拦截请求，将之前在服务器内存中进行 Session 创建销毁的动作，改成在 Redis 中创建。
        session.setAttribute("wx_open_state", state);

        //使用占位符对url地址进行拼接
        String qrcodeUrl = String.format(baseUrl, ucenterProperties.getAppId(), redirectUri, state);

        //跳转到组装的url地址中去 获得登录二维码
        return "redirect:" + qrcodeUrl;
    }

    //用户确认登录之后携带code临时票据调用回调请求  在配置文件中已经进行配置 会自动发起回调
    @GetMapping("/callback")
    public String callback(String code, String state, HttpSession session) {  //code是微信调用回调函数携带的 state是服务端向微信服务端发送的
        System.out.println("callback被调用");
        System.out.println("code:" + code);
        System.out.println("state:" + state);
        //对微信调用回调接口所携带的参数进行校验  校验的是请求微信接口时随机生成的state  校验
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(state)) {
            log.error("非法回调");
            throw new CollegeException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }
        //判断session中的state与携带的是否一致(实质是从redis中获取)                 对比
        String wxOpenState = (String) session.getAttribute("wx_open_state");
        if (!wxOpenState.equals(state)) {
            log.error("非法回调请求");
            throw new CollegeException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }
        //通过HttpClient工具类请求微信Api接口返回access_token
        String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
        //组装参数：?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        Map<String, String> accessTokenParam = new HashMap<>();
        accessTokenParam.put("appid", ucenterProperties.getAppId());
        accessTokenParam.put("secret", ucenterProperties.getAppSecret());
        accessTokenParam.put("code", code);
        accessTokenParam.put("grant_type", "authorization_code");
        HttpClientUtils clientUtils = new HttpClientUtils(accessTokenUrl, accessTokenParam);
        //发送请求
        String result = "";
        /**
         * 正确响应的JSON返回结果 :
         * "access_token":"ACCESS_TOKEN",
         * "expires_in":7200,
         * "refresh_token":"REFRESH_TOKEN",
         * "openid":"OPENID",
         * "scope":"SCOPE",
         * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
         *
         * 错误响应的JSON返回结果: 错误码和错误信息
         * {"errcode":40029,"errmsg":"invalid code"}
         */
        try {
            clientUtils.get();
            //得到的是json字符串 需要解析为java对象
            result = clientUtils.getContent();
            System.out.println(result);
        } catch (Exception e) {
            log.error("获取access_token失败");
            throw new CollegeException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        Gson gson = new Gson();
        //将 JSON 字符串转换为 Java 对象   HashMap.class指定要将JSON字符串转换为的目标Java对象的类型
        HashMap<String, Object> resultMap = gson.fromJson(result, HashMap.class);
        //失败响应   当存在errcode时表示失败响应
        Object errCode = resultMap.get("errcode");
        if (errCode != null) {
            Double errcode = (Double) errCode;
            String errmsg = (String) resultMap.get("errmsg");
            log.error("获取access_token失败：" + "code：" + errcode + ", message：" + errmsg);
            throw new CollegeException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        //成功响应
        String access_token = (String) resultMap.get("access_token");
        String openid = (String) resultMap.get("openid");

        //得到access_token之后再次调用Api接口获取用户个人信息
        //1.先在数据库中根据openid查找有没有对应的用户信息 如果没有则相当于使用微信信息进行新用户的注册
        Member member = memberService.getMemberByOpenid(openid);
        //如果不存在对应的用户信息 调用Api接口进行注册
        /**
         * {
         * "openid":"OPENID",
         * "nickname":"NICKNAME",
         * "sex":1,
         * "province":"PROVINCE",
         * "city":"CITY",
         * "country":"COUNTRY",
         * "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
         * "privilege":[
         * "PRIVILEGE1",
         * "PRIVILEGE2"
         * ],
         * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
         * }
         */
        if (member == null) {
            //调用获取用户信息接口
            String baseUrl = "https://api.weixin.qq.com/sns/userinfo";
            //组装参数 access_token=ACCESS_TOKEN&openid=OPENID
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("access_token", access_token);
            hashMap.put("openid", openid);
            HttpClientUtils client = new HttpClientUtils(baseUrl, hashMap);
            //调用
            String memberInfo = "";
            try {
                client.get();
                memberInfo = client.getContent();
            } catch (Exception e) {
                log.error(ExceptionUtils.getMessage(e));
                throw new CollegeException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            //解析JSON数据
            HashMap<String, Object> map = gson.fromJson(memberInfo, HashMap.class);
            //失败的响应结果
            errCode = map.get("errcode");
            if (errCode != null) {
                Double errcode = (Double) errCode;
                String errmsg = (String) resultMap.get("errmsg");
                log.error("获取用户信息失败：" + "code：" + errcode + ", message：" + errmsg);
                throw new CollegeException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            //进行用户注册
            String nickname = (String) map.get("nickname");
            Double sex = (Double) map.get("sex");
            String avatar = (String) map.get("headimgurl");

            //在本地数据库中插入当前微信用户的信息（使用微信账号在本地服务器注册新用户）
            member = new Member();
            member.setOpenid(openid);
            member.setNickname(nickname);
            member.setAvatar(avatar);
            member.setSex(sex.intValue());
            memberService.save(member);
        }

        //则直接使用当前用户的信息登录（生成jwt）
        //member =>Jwt
        JwtInfo jwtInfo = new JwtInfo();
        jwtInfo.setId(member.getId());
        jwtInfo.setNickname(member.getNickname());
        jwtInfo.setAvatar(member.getAvatar());
        String jwtToken = JwtUtils.getJwtToken(jwtInfo, 1800);

        //通过url地址携带token传输到前端
        return "redirect:http://localhost:3000?token=" + jwtToken;
    }
}
