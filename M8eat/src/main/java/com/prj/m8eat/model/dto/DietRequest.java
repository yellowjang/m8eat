package com.prj.m8eat.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class DietRequest {
	private int userNo;
	private String mealType;
	private MultipartFile file; //업로드용.. Request 시 사용
	private String filePath; //DB에서 불러올 실제 경로
	private List<Food> foods;
	
	
	public String getMealType() {
		return mealType;
	}
	public void setMealType(String mealType) {
		this.mealType = mealType;
	}
	public int getUserNo() {
		return userNo;
	}
	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public List<Food> getFoods() {
		return foods;
	}
	public void setFoods(List<Food> foods) {
		this.foods = foods;
	}
	
	

	
	
}
