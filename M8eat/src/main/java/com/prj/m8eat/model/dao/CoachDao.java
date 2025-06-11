package com.prj.m8eat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.prj.m8eat.model.dto.User;

@Mapper
public interface CoachDao {
	public List<User> selectAllUsers();
	
	List<User> selectUsersByCoachNo(@Param("coachNo") int coachNo);
}
