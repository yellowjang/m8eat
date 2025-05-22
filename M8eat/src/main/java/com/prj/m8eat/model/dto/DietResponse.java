package com.prj.m8eat.model.dto;

import java.util.List;

public class DietResponse {

    private int dietNo;
    private int userNo;
    private String filePath;
    private String regDate;
    private String mealType;

    // ✅ DietsFood로 수정 (실제 섭취한 음식 정보 리스트)
    private List<DietsFood> foods;

    public DietResponse() {
    }

    public DietResponse(int dietNo, int userNo, String filePath, String regDate, String mealType) {
        this.dietNo = dietNo;
        this.userNo = userNo;
        this.filePath = filePath;
        this.regDate = regDate;
        this.mealType = mealType;
    }

    public int getDietNo() {
        return dietNo;
    }

    public void setDietNo(int dietNo) {
        this.dietNo = dietNo;
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public List<DietsFood> getFoods() {
        return foods;
    }

    public void setFoods(List<DietsFood> foods) {
        this.foods = foods;
    }
}
