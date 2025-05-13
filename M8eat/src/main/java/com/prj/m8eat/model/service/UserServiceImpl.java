package com.prj.m8eat.model.service;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.UserDao;
import com.prj.m8eat.model.dto.User;

@Service
public class UserServiceImpl implements UserService {
	
	private final UserDao userDao;
	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public int signup(User user) {
		return userDao.insertUser(user);
	}

}
