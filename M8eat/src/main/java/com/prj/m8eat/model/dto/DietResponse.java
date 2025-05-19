package com.prj.m8eat.model.dto;

import java.util.List;

public class DietResponse {
	private int dietNo;
	private int userNo;
	private String filePath;
	private String regDate;
	private List<Food> foods;
	
	public DietResponse(int dietNo, int userNo, String filePath, String regDate) {
		this.dietNo = dietNo;
		this.userNo = userNo;
		this.filePath = filePath;
		this.regDate = regDate;
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
	public List<Food> getFoods() {
		return foods;
	}
	public void setFoods(List<Food> foods) {
		this.foods = foods;
	}
	
	
}
