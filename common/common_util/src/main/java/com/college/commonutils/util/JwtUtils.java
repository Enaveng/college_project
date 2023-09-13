package com.college.commonutils.util;

import io.jsonwebtoken.*;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JwtUtils {

    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";

    //对原始的APP_SECRET进行进一步的加密
    private static Key getKeyInstance() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] bytes = DatatypeConverter.parseBase64Binary(APP_SECRET);
        return new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());
    }

    public static String getJwtToken(JwtInfo jwtInfo, int expire) {

        String JwtToken = Jwts.builder()
                //第一部分 JWT头 header
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //第二部分为有效载荷  Playload
                //存在七个默认字段  均是可选字段
                .setSubject("college-user")//主题
                .setIssuedAt(new Date())//颁发时间
                .setExpiration(DateTime.now().plusSeconds(expire).toDate())//过期时间
                //自定义的私有字段
                .claim("id", jwtInfo.getId())//用户id
                .claim("nickname", jwtInfo.getNickname())//用户昵称
                .claim("avatar", jwtInfo.getAvatar())//用户头像
                //第三部分：签名哈希 VERIFY SIGNATURE
                .signWith(SignatureAlgorithm.HS256, getKeyInstance())
                //.compact()可以将三部分进行组装
                .compact();
        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     *
     * @param jwtToken
     * @return
     */
    public static boolean checkJwtTToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     *
     * @param request
     * @return
     */
    public static boolean checkJwtTToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if (StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token获取会员id
     * HttpServletRequest是Java Servlet API中的一个接口，用于表示HTTP请求。
     * 它提供了访问HTTP请求的方法和属性，包括请求方法、请求URL、请求头、请求参数、请求体等。
     */

    public static JwtInfo getMemberIdByJwtToken(HttpServletRequest request) {
        //获取请求头
        String jwtToken = request.getHeader("token");
        if (StringUtils.isEmpty(jwtToken)) {
            return null;
        }
        //创建解析器
        JwtParser parser = Jwts.parser();
        Jws<Claims> claimsJws = parser.setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        JwtInfo jwtInfo = new JwtInfo(claims.get("id").toString(), claims.get("nickname").toString(), claims.get("avatar").toString());

        return jwtInfo;
    }

}
