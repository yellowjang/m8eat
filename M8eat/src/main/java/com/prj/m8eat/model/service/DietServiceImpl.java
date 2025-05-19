package com.prj.m8eat.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.DietDao;
import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.Food;

@Service
public class DietServiceImpl implements DietService {
	
	private final DietDao dietDao;
	public DietServiceImpl(DietDao dietDao) {
		this.dietDao = dietDao;
	}

	@Override
	public List<DietResponse> getAllDiets() {
		List<DietResponse> dietList = new ArrayList<>();
		Map<Integer, DietResponse> dietMap = new HashMap<>();

		List<Diet> diets = dietDao.selectAllDiets();
		for (Diet diet : diets) {
			DietResponse res = new DietResponse(diet.getDietNo(), diet.getUserNo(), diet.getFilePath(), diet.getRegDate(), diet.getMealType());;
			res.setFoods(new ArrayList<>());
			dietList.add(res);
			dietMap.put(diet.getDietNo(), res);
		}
		
		List<DietsFood> dietsFood = dietDao.selectAllDietsFood();
		for (DietsFood food : dietsFood) {
			DietResponse target = dietMap.get(food.getDietNo());
			Food tmp = new Food(food.getFoodName(), food.getCalorie());
			target.getFoods().add(tmp);
		}
		
		System.out.println("service " + dietList);
		
		return dietList;
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
