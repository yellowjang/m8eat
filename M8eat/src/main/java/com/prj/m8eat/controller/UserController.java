package com.prj.m8eat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.model.dto.LoginResponse;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.dto.UserHealthInfo;
import com.prj.m8eat.model.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
public class UserController {
	
	private final UserService userService;
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/auth/signup")
	public ResponseEntity<Void> signup(@ModelAttribute User user, @ModelAttribute UserHealthInfo healthInfo) {
		if (userService.signup(user, healthInfo) == 1) {
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/auth/login")
	public ResponseEntity<String> login(@ModelAttribute User user, HttpSession session) {
		LoginResponse result = userService.login(user);
		if (result.isLogin()) {
			User loginUser = result.getUser();
			loginUser.setPassword(null);
			session.setAttribute("loginUser", loginUser);
			return ResponseEntity.ok(result.getMessage());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result.getMessage());
		}
	}
	

	@PostMapping("/auth/logout")
	public ResponseEntity<Void> logout(HttpSession session) {
		session.invalidate();
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
	
}
