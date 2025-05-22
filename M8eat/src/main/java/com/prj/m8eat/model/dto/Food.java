package com.prj.m8eat.model.dto;

// DB에 저장된 고정 음식 정보, 계산 X
public class Food {
    private int foodId;
    private String nameKo;
    private int calories;
    private double protein;
    private double fat;
    private double carbohydrate;
    private double sugar;
    private double cholesterol;
	public int getFoodId() {
		return foodId;
	}
	public void setFoodId(int foodId) {
		this.foodId = foodId;
	}
	public String getNameKo() {
		return nameKo;
	}
	public void setNameKo(String nameKo) {
		this.nameKo = nameKo;
	}
	public int getCalories() {
		return calories;
	}
	public void setCalories(int calories) {
		this.calories = calories;
	}
	public double getProtein() {
		return protein;
	}
	public void setProtein(double protein) {
		this.protein = protein;
	}
	public double getFat() {
		return fat;
	}
	public void setFat(double fat) {
		this.fat = fat;
	}
	public double getCarbohydrate() {
		return carbohydrate;
	}
	public void setCarbohydrate(double carbohydrate) {
		this.carbohydrate = carbohydrate;
	}
	public double getSugar() {
		return sugar;
	}
	public void setSugar(double sugar) {
		this.sugar = sugar;
	}
	public double getCholesterol() {
		return cholesterol;
	}
	public void setCholesterol(double cholesterol) {
		this.cholesterol = cholesterol;
	}
	public Food(int foodId, String nameKo, int calories, double protein, double fat, double carbohydrate, double sugar,
			double cholesterol) {
		super();
		this.foodId = foodId;
		this.nameKo = nameKo;
		this.calories = calories;
		this.protein = protein;
		this.fat = fat;
		this.carbohydrate = carbohydrate;
		this.sugar = sugar;
		this.cholesterol = cholesterol;
	}
	public Food() {
	}
    
    
}
