package com.prj.m8eat.model.service;

import java.util.List;

import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.User;

public interface CoachService {
	//모든 유저들 불러오기
	public List<User> getAllUsers(); 
	//내 회원 리스트 가져오기
	List<User> getUsersManagedByCoach(int coachNo);
	
	//회원의 식단 가져오기 
//	public List<DietResponse> getUserDiets(int userNo);
	
	// 회원 끊기
	public void deleteMember(int userNo);
}
