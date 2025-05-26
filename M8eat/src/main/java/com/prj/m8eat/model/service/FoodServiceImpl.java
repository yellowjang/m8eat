package com.prj.m8eat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.FoodDao;
import com.prj.m8eat.model.dto.Food;

@Service
public class FoodServiceImpl implements FoodService{
	private final FoodDao foodDao;
	
	public FoodServiceImpl(FoodDao foodDao) {
		this.foodDao = foodDao;
	}

	@Override
	public List<Food> getAllFoods() {
		return foodDao.selectAllFoods();
	}

}
