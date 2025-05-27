package com.prj.m8eat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.Food;

@Mapper
public interface FoodDao {
    List<Food> selectAllFoods();
//    Food selectFoodById(int foodId);

	DietsFood selectDietsFoodByDietNo(int foodId);

	Food selectFoodById(int foodId);
}