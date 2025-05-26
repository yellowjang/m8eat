package com.prj.m8eat.model.dto;

public class SignupRequestDTO {
    private User user;
    private UserHealthInfo healthInfo;

    
    
    public SignupRequestDTO() {
	}
	public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public UserHealthInfo getHealthInfo() {
        return healthInfo;
    }
    public void setHealthInfo(UserHealthInfo healthInfo) {
        this.healthInfo = healthInfo;
    }
}

