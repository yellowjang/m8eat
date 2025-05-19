package com.prj.m8eat.model.dto;

public class Food {
	private String foodName;
	private int calorie;
	
	public Food(String foodName, int calorie) {
		this.foodName = foodName;
		this.calorie = calorie;
	}
	
	public String getFoodName() {
		return foodName;
	}
	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}
	public int getCalorie() {
		return calorie;
	}
	public void setCalorie(int calorie) {
		this.calorie = calorie;
	}
	
	
}
