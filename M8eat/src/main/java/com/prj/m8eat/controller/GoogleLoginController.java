package com.prj.m8eat.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.jwt.JwtUtil;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.service.GoogleOauthService;
import com.prj.m8eat.model.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/oauth/google")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class GoogleLoginController {

    private GoogleOauthService googleOauthService = new GoogleOauthService();

	private final JwtUtil util;
	private final UserService userService;
    public GoogleLoginController(GoogleOauthService googleOauthService, JwtUtil util, UserService userService) {
		this.googleOauthService = googleOauthService;
		this.util = util;
		this.userService = userService;
	}
    
    @Value("${frontend.url}")
    private String frontendUrl;

	@GetMapping("/login")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String loginUrl = googleOauthService.getGoogleLoginUrl();
        response.sendRedirect(loginUrl);
    }

    @GetMapping("/callback")
    public void handleCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
    	System.out.println("handleCallbackkkkkkkkkkkkkkkkkkkkkkkkk");

    	// 구글에서 사용자 정보 가져오기
    	User loginUser = googleOauthService.handleGoogleCallback(code);

        if (loginUser != null) {
            // JWT 토큰 발급
            String token = util.createToken(loginUser);

            // HttpOnly 쿠키로 토큰 설정
            ResponseCookie cookie = ResponseCookie.from("access-token", token)
                .httpOnly(true)                // JS 접근 불가
                .secure(true)                // HTTPS 환경이면 true로 설정
                .sameSite("None")              // 크로스 도메인 요청 시 Lax or None
                .path("/")                    // 모든 경로에서 유효
                .maxAge(60 * 60)              // 1시간
                .build();
            
            response.setHeader("Set-Cookie", cookie.toString());
            
            
            response.sendRedirect(frontendUrl);
        } else {
            // 로그인 실패 시 프론트에 에러 메시지 전달
        	response.sendRedirect(frontendUrl + "/login?error=oauth_failed");
        }
        
        
//		if (loginUser != null) {
////			session.setAttribute("loginUser", loginUser);
////			return ResponseEntity.ok("구글 로그인 성공: " + loginUser.getName());
//	        // ✅ 프론트에서 쓸 수 있게 JSON 형태로 응답
//	        Map<String, Object> response = new HashMap<>();
//	        response.put("user", loginUser);
//
//	        // 👉 JWT 토큰을 생성해서 같이 주는 것도 가능
//	        String jwt = util.createToken(loginUser);
//	        String redirectUri = "http://localhost:5173/login?token=" + jwt;
//	        response.sendRedirect(redirectUri);
////	        response.put("access-token", jwt);
//
//	        return ResponseEntity.ok(response);
//		} else {
//			return ResponseEntity.badRequest().body(Map.of("message", "구글 로그인 실패"));
//		}
    }
}
