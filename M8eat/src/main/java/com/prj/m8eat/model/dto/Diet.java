package com.prj.m8eat.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class Diet {
	private int dietNo;
	private int userNo;
	private String regDate;
	private String mealType;
	private MultipartFile file; //업로드용.. Request 시 사용
	private String filePath; //DB에서 불러올 실제 경로
	
	
	
	public Diet() {
	}

	public Diet(int userNo, MultipartFile file, String filePath) {
		this.userNo = userNo;
		this.file = file;
		this.filePath = filePath;
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
	
	@Override
	public String toString() {
		return "Diet [dietNo=" + dietNo + ", userNo=" + userNo + ", regDate=" + regDate + ", file=" + file
				+ ", filePath=" + filePath + "]";
	}
	
	
	
}
