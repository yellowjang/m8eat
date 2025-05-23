package com.prj.m8eat.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.jwt.JwtUtil;
import com.prj.m8eat.model.dto.LoginResponse;
import com.prj.m8eat.model.dto.SignupRequestDTO;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.dto.UserHealthInfo;
import com.prj.m8eat.model.service.UserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {
	
	private final UserService userService;
	private final JwtUtil util;
	public UserController(UserService userService, JwtUtil util) {
		this.userService = userService;
		this.util = util;
	}
	
	@PostMapping("/auth/signup")
//	public ResponseEntity<Void> signup(@RequestBody User user, @RequestBody UserHealthInfo healthInfo) {
	public ResponseEntity<Void> signup(@RequestBody SignupRequestDTO reqDto) {
		System.out.println("Controller signuppppppppppppppppppppppp");
		User user = reqDto.getUser();
		UserHealthInfo healthInfo = reqDto.getHealthInfo();
		System.out.println(user);
		if (userService.signup(user, healthInfo) == 1) {
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/auth/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody User user, HttpServletResponse response) {
		System.out.println("controller login " + user);
		LoginResponse result = userService.login(user);
		
		// 로그인 성공
		if (result.isLogin()) { 
	        // 1. JWT 생성
	        String jwt = util.createToken(result.getUser());

	        // 2. HttpOnly 쿠키로 설정
	        ResponseCookie cookie = ResponseCookie.from("access-token", jwt)
	            .httpOnly(true)
	            .secure(false) // 운영 시 true + https 필요
	            .sameSite("Lax")
	            .path("/")
	            .maxAge(60 * 60) // 1시간
	            .build();

	        // 3. 쿠키 전송
	        response.setHeader("Set-Cookie", cookie.toString());

	        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
		} else {  // 로그인 실패
	        Map<String, Object> resMap = new HashMap<>();
	        resMap.put("message", result.getMessage());
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resMap);
		}
	}
	
	@GetMapping("/auth/check")
	public ResponseEntity<User> checkLogin(@CookieValue("access-token") String token) {
	    if (util.validate(token)) {
	        Claims claims = util.getClaims(token);
	        User user = new User();
	        user.setUserNo((Integer) claims.get("userNo"));
	        user.setName((String) claims.get("name"));
	        user.setId((String) claims.get("id"));
	        user.setRole((String) claims.get("role"));
	        return ResponseEntity.ok(user);
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	}

	
	@PostMapping("/auth/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {
	    // ✅ access-token 쿠키를 빈 값 + 만료로 설정
	    ResponseCookie expiredCookie = ResponseCookie.from("access-token", "")
	        .httpOnly(true)
	        .secure(false)
	        .sameSite("Lax")
	        .path("/")
	        .maxAge(0) // ⛔ 즉시 만료
	        .build();
	    
	    // ✅ 쿠키 헤더 설정
	    response.setHeader("Set-Cookie", expiredCookie.toString());
	    
//		session.invalidate();
		return ResponseEntity.ok().build();
	}
	
	
	@DeleteMapping("/auth/quit/{userNo}")
	public ResponseEntity<String> quit(@PathVariable("userNo") int userNo) {
		int result = userService.quit(userNo);
		if (result == 1) {
			return ResponseEntity.status(HttpStatus.OK).body("정상적으로 탈퇴되었습니다.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("요청이 정상적으로 처리되지 않았습니다.");
		}
	}
	
	@GetMapping("/user/mypage")
	public ResponseEntity<User> getMyInfo(HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");
		User myInfo = userService.getMyInfo(loginUser.getId());
		if (myInfo != null) {
			return ResponseEntity.ok(myInfo);
		}
		return ResponseEntity.notFound().build();
	}
	
	@PutMapping("/user/mypage/{userNo}")
	public ResponseEntity<String> updateMyInfo(@PathVariable("userNo") int userNo, @ModelAttribute User user) {
		user.setUserNo(userNo);
		if (userService.updateMyInfo(user) == 1) {
			return ResponseEntity.ok().body("정상적으로 수정되었습니다.");
		}
		return ResponseEntity.badRequest().body("요청이 정상적으로 처리되지 않았습니다.");
	}
	
	@PutMapping("/user/mypage/health-info")
	public ResponseEntity<String> updateHealthInfo(@ModelAttribute UserHealthInfo userHealthInfo, HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");
		userHealthInfo.setUserNo(loginUser.getUserNo());
		if (userService.updateHealthInfo(userHealthInfo) == 1) {
			return ResponseEntity.status(HttpStatus.CREATED).body("수정되었습니다.");
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("요청이 정상적으로 처리되지 않았습니다.");
	}
	
}
