package com.prj.m8eat.model.dto;

public class LoginResponse {
	
	private boolean isLogin;
	private String message;
	private String id;
	
	public LoginResponse(boolean isLogin, String message, String id) {
		this.isLogin = isLogin;
		this.message = message;
		this.id = id;
	}
	
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
