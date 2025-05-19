package com.prj.m8eat.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleOauthService {

	@Value("${google.client-id}")
	private String clientId;

	@Value("${google.client-secret}")
	private String clientSecret;

	@Value("${google.redirect-uri}")
	private String redirectUri;

	@Value("${google.token-uri}")
	private String tokenUri;

	@Value("${google.user-info-uri}")
	private String userInfoUri;

	private final RestTemplate restTemplate = new RestTemplate();

	public String getGoogleLoginUrl() {
		return "https://accounts.google.com/o/oauth2/v2/auth" + "?client_id=" + clientId + "&redirect_uri="
				+ redirectUri + "&response_type=code" + "&scope=email%20profile" + "&access_type=offline";
	}

	public String handleGoogleCallback(String code) {
		// 1. 토큰 요청
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("grant_type", "authorization_code");

		HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);

		ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUri, tokenRequest, Map.class);
		String accessToken = (String) tokenResponse.getBody().get("access_token");

		// 2. 사용자 정보 요청
		HttpHeaders userInfoHeaders = new HttpHeaders();
		userInfoHeaders.setBearerAuth(accessToken);

		HttpEntity<?> userInfoRequest = new HttpEntity<>(userInfoHeaders);

		ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUri, HttpMethod.GET, userInfoRequest,
				Map.class);

		Map<String, Object> googleUser = userInfoResponse.getBody();
		String email = (String) googleUser.get("email");
		System.out.println(email);
		System.out.println(accessToken);
		// 3. JWT 발급 (또는 회원가입/로그인 처리)
		return createJwt(email);
	}

	private String createJwt(String email) {
		return "jwt-token-for-google-user-" + email;
	}

}
