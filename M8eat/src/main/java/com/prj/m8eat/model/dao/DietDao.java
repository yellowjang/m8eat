package com.prj.m8eat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietsFood;

@Mapper
public interface DietDao {

	public List<Diet> selectAllDiets();
	
	public List<DietsFood> selectAllDietsFood();

	public int insertDiet(Diet diet);

	public void insertDietsFood(DietsFood dietsFood);

}
