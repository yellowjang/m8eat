package com.prj.m8eat.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.service.KakaoOauthService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/oauth/kakao")
public class KakaoLoginController {

	private final KakaoOauthService kakaoOauthService;

	public KakaoLoginController(KakaoOauthService kakaoOauthService) {
		this.kakaoOauthService = kakaoOauthService;
	}

	@GetMapping("/login")
	public ResponseEntity<String> kakaoLogin() {
		String kakaoLoginUrl = kakaoOauthService.getKakaoLoginUrl();
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, kakaoLoginUrl).build();
	}

	@GetMapping("/callback")
	public ResponseEntity<String> kakaoCallback(@RequestParam String code,HttpSession session) {
		User loginUser = kakaoOauthService.handleKakaoCallback(code);
		System.out.println();
		if (loginUser != null) {
			session.setAttribute("loginUser", loginUser);
			return ResponseEntity.ok("카카오 로그인 성공: " + loginUser.getName()+loginUser.getRole());
		} else {
			return ResponseEntity.badRequest().body("카카오 로그인 실패");
		}
    }
		
	
}
