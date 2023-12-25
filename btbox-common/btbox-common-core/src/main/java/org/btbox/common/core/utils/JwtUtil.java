package org.btbox.common.core.utils;

import cn.hutool.jwt.Claims;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.util.Date;

/**
 * Jwt工具类
 * Created by RubinChu on 2021/1/22 下午 4:11
 */
public class JwtUtil {

    public static final Long TWO_LONG = 2L;

    /**
     * 秘钥
     */
    private final static String JWT_PRIVATE_KEY = "0CB16040A41140E48F2F93A7BE222C46";

    /**
     * 刷新时间
     */
    private final static String RENEWAL_TIME = "RENEWAL_TIME";

    /**
     * 生成token
     *
     * @param subject
     * @param claimKey
     * @param claimValue
     * @param expire
     * @return
     */
    public static String generateToken(String subject, String claimKey, Object claimValue, Long expire) {
        String token = JWT.create()
                .setSubject(subject)
                .setExpiresAt(new Date(System.currentTimeMillis() + expire))
                .setPayload(claimKey, claimValue)
                .sign(JWTSignerUtil.hs256(JWT_PRIVATE_KEY.getBytes()));
        return token;
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     */
    public static Object analyzeToken(String token, String claimKey) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        try {
            return JWT.of(token).setKey(JWT_PRIVATE_KEY.getBytes()).getPayload(claimKey);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验token
     * @param token
     * @return
     */
    public static boolean verify(String token) {
        return JWT.of(token).setKey(JWT_PRIVATE_KEY.getBytes()).validate(0);
    }

    public static void main(String[] args) throws InterruptedException {
        String s = generateToken("btbox1", "username", "bb", 10L * 1000L);
        System.out.println("s = " + s);
        System.out.println(verify(s));
    }

}
