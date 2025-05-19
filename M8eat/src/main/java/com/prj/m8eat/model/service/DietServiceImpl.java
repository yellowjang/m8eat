package com.prj.m8eat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.DietDao;
import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.Food;

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

	@Override
	public boolean writeDiets(Diet diet, List<Food> foods) {
		System.out.println(diet);
		if (dietDao.insertDiet(diet) == 1) {
			for (Food food : foods) {
				DietsFood dietsFood = new DietsFood(diet.getDietNo(), food.getFoodName(), food.getCalorie());
				dietDao.insertDietsFood(dietsFood);
			}
			return true;
		}
		return false;

	}

}
