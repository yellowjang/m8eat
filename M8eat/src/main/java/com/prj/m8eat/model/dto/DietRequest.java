package com.prj.m8eat.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class DietRequest {
	private int userNo;
    private String mealType;
    private MultipartFile file;
    private String filePath;
    private String foods; // JSON 문자열 (프론트에서 stringify 해서 넘김)
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
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFoods() {
		return foods;
	}
	public void setFoods(String foods) {
		this.foods = foods;
	}
	public DietRequest(int userNo, String mealType, MultipartFile file, String filePath, String foods) {
		super();
		this.userNo = userNo;
		this.mealType = mealType;
		this.file = file;
		this.filePath = filePath;
		this.foods = foods;
	}
	public DietRequest() {
	}

}
