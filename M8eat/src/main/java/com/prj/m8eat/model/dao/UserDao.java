package com.prj.m8eat.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.dto.UserHealthInfo;

@Mapper
public interface UserDao {

	public int insertUser(User user);

	public int insertUserHealthInfo(UserHealthInfo healthInfo);

	public User selectUser(String id);

	public int deleteUser(int userNo);

	public int updateUserInfo(User user);

	public int updateUserHealthInfo(UserHealthInfo userHealthInfo);

	public UserHealthInfo selectHealthInfo(int userNo);

	public String selectCoachId(int userNo);

}
