package com.prj.m8eat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.service.DietService;

@RestController
@RequestMapping("/diets")
public class DietController {

	private final DietService dietService;
	public DietController(DietService dietService) {
		this.dietService = dietService;
	}
	
	// 식단 전체 조회
	@GetMapping
	public ResponseEntity<?> getAllDiets() {
		List<Diet> dietList = dietService.getAllDiets();
		System.out.println(dietList);
		if (dietList == null || dietList.size() == 0) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Diet>>(dietList, HttpStatus.OK);
	}
	
	
	
}
