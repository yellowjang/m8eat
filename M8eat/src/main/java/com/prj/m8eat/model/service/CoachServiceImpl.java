package com.prj.m8eat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.CoachDao;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.User;

@Service
public class CoachServiceImpl implements CoachService {
	private final CoachDao coachDao;
	
	public CoachServiceImpl(CoachDao coachDao) {
		this.coachDao = coachDao;
	}

	

	@Override
	public List<User> getAllUsers() {
		return coachDao.selectAllUsers();
	}

	@Override
	public List<User> getUsersManagedByCoach(int coachNo) {
	    return coachDao.selectUsersByCoachNo(coachNo);
	}

//	@Override
//	public List<DietResponse> getUserDiets(int userNo) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public void deleteMember(int userNo) {
		// TODO Auto-generated method stub
		
	}

}
