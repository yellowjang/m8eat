package com.prj.m8eat.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prj.m8eat.jwt.JwtUtil;
import com.prj.m8eat.model.dto.LoginResponse;
import com.prj.m8eat.model.dto.SignupRequestDTO;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.dto.UserHealthInfo;
import com.prj.m8eat.model.service.UserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
	
	@Value("${file.upload.dir}")
	private String uploadDirPath;
	
	@PostMapping(value = "/auth/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> signup(@RequestPart("user") User user,
		    						   @RequestPart("healthInfo") UserHealthInfo healthInfo,
		    						   @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
		System.out.println("Controller signuppppppppppppppppppppppp");
		System.out.println("👤 user: " + user);
	    System.out.println("💪 healthInfo: " + healthInfo);
	    System.out.println("🖼️ profileImage: " + (profileImage != null ? profileImage.getOriginalFilename() : "없음"));
	    System.out.println(profileImage);
		
		// 예: 프로필 이미지 경로 출력
		if (profileImage != null && !profileImage.isEmpty()) {
			System.out.println("🖼️ 프로필 이미지: " + profileImage);
			String originalFilename = profileImage.getOriginalFilename();
//			String uploadDirPath = "/Users/jang-ayoung/Desktop/m8eat/data";

			File uploadDir = new File(uploadDirPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}

			try {
				File saveFile = new File(uploadDir, originalFilename);
				profileImage.transferTo(saveFile);
				user.setProfileImagePath("/upload/" + originalFilename); // 웹에서 접근 가능한 경로로 구성 (정적 리소스 매핑 필요)
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
			}
		}
		System.out.println("userrrr " + user);
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
		
		System.out.println("JWTTTTTTTT" + result);
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
	        System.out.println("checkkkk" + claims);
	        User user = new User();
	        user.setUserNo((Integer) claims.get("userNo"));
	        user.setName((String) claims.get("name"));
	        user.setId((String) claims.get("id"));
	        user.setRole((String) claims.get("role"));
	        user.setProfileImagePath((String) claims.get("profileImagePath"));
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
	public ResponseEntity<String> quit(@PathVariable("userNo") int userNo, HttpServletResponse response) {
		System.out.println(userNo);
		int result = userService.quit(userNo);
		if (result == 1) {
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

	@GetMapping("/user/mypage/healthInfo")
	public ResponseEntity<?> getMyHealthInfo(@CookieValue("access-token") String token) {
		System.out.println("healthhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
		int userNo;
	    if (util.validate(token)) {
	        Claims claims = util.getClaims(token);
	        userNo = (int) claims.get("userNo");
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	    
	    System.out.println("healthhhh" + userNo);

		UserHealthInfo healthInfo = userService.getMyHealthInfo(userNo);
		if (healthInfo != null) {
			return ResponseEntity.ok(healthInfo);
		}
		return ResponseEntity.notFound().build();
	}
	
	@PutMapping("/user/mypage/{userNo}")
	public ResponseEntity<String> updateMyInfo(@PathVariable("userNo") int userNo, @RequestBody User user) {
		System.out.println("infouupdateeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		System.out.println("updateeeee" + user);
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
	
	@GetMapping("/user/mypage/coachId")
	public ResponseEntity<?> getCoachId(@CookieValue("access-token") String token) {
		int userNo;
	    if (util.validate(token)) {
	        Claims claims = util.getClaims(token);
	        userNo = (int) claims.get("userNo");
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    String coachId = userService.getCoachId(userNo);
		System.out.println("coachhhhhh" + coachId);
		if (coachId != null) {
			return ResponseEntity.ok(coachId);
		}
		return ResponseEntity.notFound().build();
	}
	
}
