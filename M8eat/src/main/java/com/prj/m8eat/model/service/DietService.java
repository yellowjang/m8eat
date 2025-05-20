package com.prj.m8eat.model.service;

import java.util.List;

import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.Food;

public interface DietService {

	public List<DietResponse> getAllDiets();

	public boolean writeDiets(Diet diet, List<Food> foods);

	public List<DietResponse> getDietsByUserNo(int userNo);

	public List<DietResponse> getDietsByDate(String startDate, String endDate);

	public List<DietResponse> getDietsByDietNo(int dietNo);

}
