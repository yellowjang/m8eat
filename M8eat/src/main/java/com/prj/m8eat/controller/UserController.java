package com.prj.m8eat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.model.dto.LoginResponse;
import com.prj.m8eat.model.dto.SignupRequestDTO;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.dto.UserHealthInfo;
import com.prj.m8eat.model.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin("*")
public class UserController {
	
	private final UserService userService;
	public UserController(UserService userService) {
		this.userService = userService;
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
	public ResponseEntity<String> login(@RequestBody User user, HttpSession session) {
		System.out.println("controller login " + user);
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
