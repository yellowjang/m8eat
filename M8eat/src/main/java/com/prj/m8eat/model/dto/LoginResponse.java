package com.prj.m8eat.model.dto;

public class LoginResponse {
	
	private boolean isLogin;
	private String message;
	private User user;
	
	public LoginResponse(boolean isLogin, String message, User user) {
		this.isLogin = isLogin;
		this.message = message;
		this.user = user;
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "LoginResponse [isLogin=" + isLogin + ", message=" + message + ", user=" + user + "]";
	}
	
	
	
	
	
}
