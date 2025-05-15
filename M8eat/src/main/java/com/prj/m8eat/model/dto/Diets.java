package com.prj.m8eat.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class Diets {
	private int dietNo;
	private int userNo;
	private String regDate;
	private MultipartFile file;
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
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
}
