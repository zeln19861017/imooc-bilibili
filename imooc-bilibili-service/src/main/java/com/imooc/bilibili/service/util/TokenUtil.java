package com.imooc.bilibili.service.util;

// 使用jwt生成用户令牌
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.imooc.bilibili.domain.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {

    private static final String ISSUER = "签发者";
    // 生成
    public  static String generateToken(Long userId) throws  Exception{
        // 算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        // 日历类生成JWT过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // 30秒过期
        calendar.add(Calendar.SECOND,30);
        return  JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
    // 验证
    public static Long verifyToken(String token)  {
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
            // 创建jwt验证类
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        }catch (TokenExpiredException e){
            // 过期
            throw new ConditionException("555","token过期");
        }catch (Exception e){
            throw  new ConditionException("500","非法token");
        }

    }
}
