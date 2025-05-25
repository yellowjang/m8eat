package com.prj.m8eat.model.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public class DietRequest {
	private int dietNo;
	private int userNo;
    private String mealType;
    private MultipartFile file;
    private String filePath;
    private String foods; // JSON 문자열 (프론트에서 stringify 해서 넘김)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime mealDate;
	
	

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

	
	@Override
	public String toString() {
		return "DietRequest [dietNo=" + dietNo + ", userNo=" + userNo + ", mealType=" + mealType + ", file=" + file
				+ ", filePath=" + filePath + ", foods=" + foods + "]";
	}
	
	public LocalDateTime getMealDate() {
		return mealDate;
	}
	
	public void setMealDate(LocalDateTime mealDate) {
		this.mealDate = mealDate;
	}

	
}
