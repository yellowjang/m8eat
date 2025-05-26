package com.prj.m8eat.model.service;

import com.prj.m8eat.model.dto.LoginResponse;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.dto.UserHealthInfo;

public interface UserService {

	public int signup(User user, UserHealthInfo healthInfo);

	//소셜 로그인 
	public  User socialLogin(User user);
	
	public LoginResponse login(User user);

	public int quit(int userNo);

	public User getMyInfo(String id);

	public int updateMyInfo(User user);

	public int updateHealthInfo(UserHealthInfo userHealthInfo);

	public UserHealthInfo getMyHealthInfo(int userNo);

	public String getCoachId(int userNo);

}
