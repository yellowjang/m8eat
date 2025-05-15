package com.prj.m8eat.model.service;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.UserDao;
import com.prj.m8eat.model.dto.LoginResponse;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.dto.UserHealthInfo;

@Service
public class UserServiceImpl implements UserService {

	private final UserDao userDao;

	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public int signup(User user, UserHealthInfo healthInfo) {
		if (userDao.insertUser(user) == 1) {
			healthInfo.setUserNo(user.getUserNo());
			return userDao.insertUserHealthInfo(healthInfo);
		}
		return 0;
	}

	@Override
	public LoginResponse login(User user) {
		User isUser = userDao.selectUser(user.getId());
		if (isUser != null) { // 존재하는 아이디
			if (user.getPassword().equals(isUser.getPassword())) { // 아이디 존재, 비밀번호 일치
				return new LoginResponse(true, "로그인 되었습니다.", isUser);
			} else { // 아이디 존재, 비밀번호 불일치
				return new LoginResponse(false, " 비밀번호가 일치하지 않습니다.", null);
			}
		} else { // 존재하지 않는 아이디
			return new LoginResponse(false, "존재하지 않는 아이디입니다.", null);
		}

	}

	@Override
	public int quit(int userNo) {
		return userDao.deleteUser(userNo);
	}

	@Override
	public User getMyInfo(String id) {
		return userDao.selectUser(id);
	}

	@Override
	public int updateMyInfo(User user) {
		return userDao.updateUserInfo(user);
	}

}
