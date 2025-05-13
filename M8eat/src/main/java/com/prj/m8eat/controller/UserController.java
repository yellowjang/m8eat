package com.prj.m8eat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.service.UserService;

@RestController
public class UserController {
	
	private final UserService userService;
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/auth/signup")
	public ResponseEntity<Void> signup(@ModelAttribute User user) {
		if (userService.signup(user) == 1) {
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
}
