package com.prj.m8eat.model.dto;

public class DietsFood {
    private int no;
    private int dietNo;
    private int foodId;
    private String foodName;
    private double amount; // g
    private int calorie;
    private double protein;
    private double fat;
    private double carbohydrate;
    private double sugar;
    private double cholesterol;

    public DietsFood() {}

    public DietsFood(int dietNo, int foodId, String foodName, double amount, int calorie,
                     double protein, double fat, double carbohydrate, double sugar, double cholesterol) {
        this.dietNo = dietNo;
        this.foodId = foodId;
        this.foodName = foodName;
        this.amount = amount;
        this.calorie = calorie;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
        this.sugar = sugar;
        this.cholesterol = cholesterol;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getDietNo() {
        return dietNo;
    }

    public void setDietNo(int dietNo) {
        this.dietNo = dietNo;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
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
}
