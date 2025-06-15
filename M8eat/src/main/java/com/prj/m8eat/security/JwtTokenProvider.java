package com.prj.m8eat.security;

import com.prj.m8eat.model.dto.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long validityInMilliseconds = 60 * 60 * 1000L; // 1시간

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ✅ JWT 생성
    public String createToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(validityInMilliseconds);

        return Jwts.builder()
                .subject(user.getId()) // 사용자 ID → subject로
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim("userNo", user.getUserNo())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .claim("profileImagePath", user.getProfileImagePath())
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        User user = getUserFromToken(token);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    

    // ✅ JWT에서 사용자 정보 추출
    public User getUserFromToken(String token) {
        Claims claims = getClaims(token);

        User user = new User();
        user.setUserNo((Integer) claims.get("userNo"));
        user.setName((String) claims.get("name"));
        user.setId(claims.getSubject());
        user.setRole((String) claims.get("role"));
        user.setProfileImagePath((String) claims.get("profileImagePath"));
        return user;
    }

    // ✅ 토큰 유효성 검사
    public boolean validate(String token) {
        try {
            getClaims(token); // parse 성공 시 유효
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ Claims 추출 (0.12.6 방식)
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
