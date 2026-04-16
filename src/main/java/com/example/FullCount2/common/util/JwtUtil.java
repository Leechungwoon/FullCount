package com.example.FullCount2.common.util;

import com.example.FullCount2.common.enums.UserRole;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component //Spring Bean 등록
public class JwtUtil {

    private static final String HEADER_KEY = "Authorization"; //클라이언트 요청 시 Authorization: Bearer {token} 형태로 보냄
    private static final String BEARER_PREFIX = "Bearer "; //JWT 앞에 붙는 타입
    private static final long TOKEN_TIME = 60 * 60 * 1000L; // 토큰 유효 시간(60분)
    private static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // Refresh Token 유효 시간(7일)

    @Value("${jwt.secret.key}")
    private String secretKey;

    private SecretKey key; // JWT 서명/검증에 사용할 암호화 키 객체
    private JwtParser parser; //JWT 토큰을 파싱하고 검증하는 재사용되는 파서


    @PostConstruct
    public void init() {
        byte[] bytes = Decoders.BASE64.decode(secretKey); // secret key -> byte[]로 변환
        this.key = Keys.hmacShaKeyFor(bytes); //byte 배열을 HMAC-SHA 알고리즘용 Key 객체로 변환
        this.parser = Jwts.parser() // 토큰 파싱 및 검증할 parser미리 생성 (재사용 목적)
                .verifyWith(this.key)
                .build();
    }

    //토큰 생성
    public String createToken(Long userId, String email, String name, UserRole role) {
        Date now = new Date(); //현재 시간

        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("name", name)
                .claim("role", role.name())
                .issuedAt(now) //토큰 발급 시간
                .expiration(new Date(now.getTime() + TOKEN_TIME)) //토큰 만료시간
                .signWith(key, Jwts.SIG.HS256)
                .compact(); //최종 문자열 생성

        return BEARER_PREFIX + token;
    }

    //헤더에서 순수 토큰 추출 (Bearer제거)
    public String extractToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    //토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            parser.parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //토큰 userId 추출
    public Long getUserId(String token) {
        return Long.parseLong(
                parser.parseSignedClaims(token).getPayload().getSubject()
        );
    }

    //토큰에서 role 추출
    public UserRole getRole(String token) {
        String role = parser.parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
        return UserRole.valueOf(role);
    }
}
