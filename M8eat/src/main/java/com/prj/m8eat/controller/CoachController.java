package com.prj.m8eat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.jwt.JwtUtil;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.service.CoachService;
import com.prj.m8eat.model.service.DietService;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/coach")
public class CoachController {

	private final CoachService coachService;
	private final DietService dietService;
	private final JwtUtil util;

	public JwtUtil getUtil() {
		return util;
	}

	public CoachController(CoachService coachService, JwtUtil util, DietService dietService) {
		this.coachService = coachService;
		this.dietService = dietService;
		this.util = util;
	}

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		List<User> userList = coachService.getAllUsers();
		System.out.println("유저 리스트 불러오기 ");
		if (userList == null || userList.size() == 0) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(userList);
	}

	@GetMapping("/members")
	public ResponseEntity<List<User>> getMyMembers(@CookieValue("access-token") String token) {
		System.out.println("asdhadsfasdhgasddgasdf");
	    if (!util.validate(token)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    Claims claims = util.getClaims(token);
	    int coachNo = (int) claims.get("userNo");
	    System.out.println("코치넘버"+coachNo);
	    List<User> members = coachService.getUsersManagedByCoach(coachNo);
	    return ResponseEntity.ok(members);
	}
	
	@GetMapping("/member/{userNo}/diets")
	public ResponseEntity<?> getMemberDiets(
	        @PathVariable int userNo,
	        @CookieValue("access-token") String token) {

	    // 1. 토큰 검증
	    if (!util.validate(token)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    // 2. 내 userNo (coachNo) 파싱
	    int coachNo = (int) util.getClaims(token).get("userNo");

	    // 3. 해당 user가 내 회원인지 확인
	    List<User> myMembers = coachService.getUsersManagedByCoach(coachNo);
	    boolean isMyUser = myMembers.stream().anyMatch(user -> user.getUserNo() == userNo);

	    if (!isMyUser) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body("해당 유저는 당신의 회원이 아닙니다.");
	    }

	    // 4. 식단 조회
	    List<DietResponse> diets = dietService.getDietsByUserNo(userNo);
	    return ResponseEntity.ok(diets);
	}

}
