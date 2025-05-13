package com.prj.m8eat.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.prj.m8eat.model.dto.User;

@Mapper
public interface UserDao {

	public int insertUser(User user);

	public User selectUser(String id);

}
