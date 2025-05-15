package com.prj.m8eat.model.service;

import com.prj.m8eat.model.dto.LoginResponse;
import com.prj.m8eat.model.dto.User;

public interface UserService {

	public int signup(User user);

	public LoginResponse login(User user);

	public int quit(int userNo);

}
