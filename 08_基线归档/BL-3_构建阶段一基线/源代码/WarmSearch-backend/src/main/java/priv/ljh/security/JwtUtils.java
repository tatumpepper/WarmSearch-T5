package priv.ljh.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 生成与解析
 */
@Component
public class JwtUtils {

    @Value("${warmsearch.jwt.secret}")
    private String secret;

    @Value("${warmsearch.jwt.expire}")
    private long expire;

    public String createToken(LoginUser user) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expire * 1000);
        return JWT.create()
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("name", user.getName())
                .withClaim("userType", user.getUserType())
                .withClaim("role", user.getRole())
                .withIssuedAt(now)
                .withExpiresAt(expireAt)
                .sign(Algorithm.HMAC256(secret));
    }

    public LoginUser parse(String token) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        return LoginUser.builder()
                .id(jwt.getClaim("id").asLong())
                .username(jwt.getClaim("username").asString())
                .name(jwt.getClaim("name").asString())
                .userType(jwt.getClaim("userType").asString())
                .role(jwt.getClaim("role").asString())
                .build();
    }
}
