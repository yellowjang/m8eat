package com.prj.m8eat.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietRequest;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.FoodInfo;

@Mapper
public interface DietDao {

	public List<Diet> selectAllDiets();
	
	public List<DietsFood> selectAllDietsFood();

	public int insertDiet(Diet diet);

	public void insertDietsFood(DietsFood dietsFood);

	public List<Diet> selectDietsByUserNo(int userNo);

	public List<DietsFood> selectDietsFoodByDietNo(int userNo);

	public List<Diet> selectDietsByDate(Map<String, String> map);

	public Diet selectDietsByDietNo(int dietNo);

	public int deleteDiet(int dietNo);

	public int updateDiet(Diet updateDiet);
	
	public void deleteDietFood(int dietNo);

	public List<String> getAllDietNames();

	public FoodInfo getDietByName(String name);
	


}
