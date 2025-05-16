package com.prj.m8eat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.DietDao;
import com.prj.m8eat.model.dto.Diet;

@Service
public class DietServiceImpl implements DietService {
	
	private final DietDao dietDao;
	public DietServiceImpl(DietDao dietDao) {
		this.dietDao = dietDao;
	}

	@Override
	public List<Diet> getAllDiets() {
		return dietDao.selectAllDiets();
	}

}
