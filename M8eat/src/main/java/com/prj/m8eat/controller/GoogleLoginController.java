package com.prj.m8eat.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.model.service.GoogleOauthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/oauth/google")
@RequiredArgsConstructor
public class GoogleLoginController {

    private GoogleOauthService googleOauthService = new GoogleOauthService();

    public GoogleLoginController(GoogleOauthService googleOauthService) {
		this.googleOauthService = googleOauthService;
	}

	@GetMapping("/login")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String loginUrl = googleOauthService.getGoogleLoginUrl();
        response.sendRedirect(loginUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestParam String code) {
        String jwt = googleOauthService.handleGoogleCallback(code);
        return ResponseEntity.ok().body(jwt);
     
    }
}
