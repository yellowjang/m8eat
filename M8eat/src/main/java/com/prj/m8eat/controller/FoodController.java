package com.prj.m8eat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.model.dto.Food;
import com.prj.m8eat.model.service.FoodService;

@RestController
@RequestMapping("/foods")
@CrossOrigin("*")
public class FoodController {
		private final FoodService foodService;

		public FoodController(FoodService foodService) {
			this.foodService = foodService;
		}
		
		@GetMapping
		public ResponseEntity<List<Food>> getAllFoods(){
			List<Food> foodList = foodService.getAllFoods();
			return ResponseEntity.ok(foodList);
		}
		
}
