package com.prj.m8eat.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.jwt.JwtUtil;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.service.KakaoOauthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/oauth/kakao")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class KakaoLoginController {

	private final KakaoOauthService kakaoOauthService;
	private final JwtUtil util;
	public KakaoLoginController(KakaoOauthService kakaoOauthService, JwtUtil util) {
		this.kakaoOauthService = kakaoOauthService;
		this.util = util;
	}

    @Value("${frontend.url}")
    private String frontendUrl;
	
	@GetMapping("/login")
	public void kakaoLogin(HttpServletResponse response) throws IOException {
		String kakaoLoginUrl = kakaoOauthService.getKakaoLoginUrl();
		response.sendRedirect(kakaoLoginUrl);
	}

	@GetMapping("/callback")
	public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
		User loginUser = kakaoOauthService.handleKakaoCallback(code);
		
        if (loginUser != null) {
            // JWT 토큰 발급
            String token = util.createToken(loginUser);

            // HttpOnly 쿠키로 토큰 설정
            ResponseCookie cookie = ResponseCookie.from("access-token", token)
                .httpOnly(true)                // JS 접근 불가
                .secure(false)                // HTTPS 환경이면 true로 설정
                .sameSite("Lax")              // 크로스 도메인 요청 시 Lax or None
                .path("/")                    // 모든 경로에서 유효
                .maxAge(60 * 60)              // 1시간
                .build();
            
            response.setHeader("Set-Cookie", cookie.toString());
            
            response.sendRedirect(frontendUrl);
        } else {
            // 로그인 실패 시 프론트에 에러 메시지 전달
        	response.sendRedirect(frontendUrl + "/login?error=oauth_failed");
        }
		
    }
		
	
}
