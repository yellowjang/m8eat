package com.prj.m8eat.model.service;

import com.prj.m8eat.model.dto.LoginResponse;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.dto.UserHealthInfo;

public interface UserService {

	public int signup(User user, UserHealthInfo healthInfo);

	public LoginResponse login(User user);

	public int quit(int userNo);

	public User getMyInfo(String id);

	public int updateMyInfo(User user);

}
