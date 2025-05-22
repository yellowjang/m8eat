package com.prj.m8eat.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class DietRequest {
    private int userNo;
    private String mealType;
    private MultipartFile file;
    private String foods; // JSON 문자열로 받음

    public int getUserNo() {
        return userNo;
    }
    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public String getMealType() {
        return mealType;
    }
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public MultipartFile getFile() {
        return file;
    }
    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFoods() {
        return foods;
    }
    public void setFoods(String foods) {
        this.foods = foods;
    }
}
