package com.prj.m8eat.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.prj.m8eat.model.dto.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
    private final SecretKey secretKey;
    public JwtUtil(@Value("${jwt.secret}") String key) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }
	
//	private String key = "SSAFY_NonMajor_JavaTrack_SecretKey";
//	private SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
	
	//토큰 생성시 다양한 데이터를 저장할 수 있음 (DTO or Map)
	public String createToken(User user) {
		//유효기간 
		Date exp = new Date(System.currentTimeMillis()+ 1000*60*60); //1시간
		return Jwts.builder().header().add("typ", "JWT").and()
				.claim("userNo", user.getUserNo())
				.claim("name", user.getName())
				.claim("id", user.getId())
				.claim("role", user.getRole())
				.expiration(exp)
				.signWith(secretKey).compact();
	}
	
    // ✅ 2. 유효성 검증
    public boolean validate(String token) {
        try {
        	System.out.println("✅ 토큰 검증 시도: " + token);
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ 3. Claims (payload) 추출
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
	
	
//	//유효성 검증 (실제로 내용물을 확인하기 위함은 아님 / 에러나면 유효기간 지난것)
//	//이거 실행했을때 에러나면 유효기간 지난거....
//	public Jws<Claims> vaildate(String token){
//		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
//	}
}
