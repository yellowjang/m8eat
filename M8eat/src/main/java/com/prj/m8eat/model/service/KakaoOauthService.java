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

import com.prj.m8eat.model.dto.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoOauthService {

	private UserService userService;

	public KakaoOauthService(UserService userService) {
		this.userService = userService;
	}

	@Value("${kakao.client-id}")
	private String clientId;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	@Value("${kakao.token-uri}")
	private String tokenUri;

	@Value("${kakao.user-info-uri}")
	private String userInfoUri;

	private final RestTemplate restTemplate = new RestTemplate();

	public String getKakaoLoginUrl() {
		return "https://kauth.kakao.com/oauth/authorize" + "?client_id=" + clientId + "&redirect_uri=" + redirectUri
				+ "&response_type=code";
	}

	public User handleKakaoCallback(String code) {
		// 1. 토큰 요청
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
		ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUri, tokenRequest, Map.class);

		String accessToken = (String) tokenResponse.getBody().get("access_token");
		//엑세스 토큰 
//		System.out.println(accessToken);
		// 2. 사용자 정보 요청
		HttpHeaders userInfoHeaders = new HttpHeaders();
		userInfoHeaders.setBearerAuth(accessToken);
		HttpEntity<?> userInfoRequest = new HttpEntity<>(userInfoHeaders);

		ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUri, HttpMethod.GET, userInfoRequest,
				Map.class);
		Map<String, Object> kakaoUser = userInfoResponse.getBody();

		// 3. 필요한 사용자 정보 추출
		Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUser.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

		String email = (String) kakaoAccount.get("email");
		String nickname = (String) profile.get("nickname");

		// 4. 유저 객체 생성 및 로그인 처리
		User user = new User();
		user.setId("kakao" + kakaoUser.get("id")); // 예: kakao12345678
		user.setName(nickname);

		return userService.socialLogin(user);
	}
}
