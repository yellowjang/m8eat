package com.prj.m8eat.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietRequest;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.Food;

public interface DietService {

	public List<DietResponse> getAllDiets();

	public boolean writeDiets(Diet diet, List<DietsFood> foodRecords);

	public List<DietResponse> getDietsByUserNo(int userNo);

	public List<DietResponse> getDietsByDate(String startDate, String endDate);

	public List<DietResponse> getDietsByDietNo(int dietNo);

	public boolean deleteDietByDietNo(int dietNo);

	public boolean updateDietByDietNo(DietRequest dietReq);

	public List<Map<String, Object>> analyzeImageWithVisionAndGpt(MultipartFile file) throws Exception;

	//여러 음식을 넣을 때, 같은 dietNo 로 들어가도록 해야함.
	public int createDietWithFoods(Diet diet, List<DietsFood> foodList);

//	public DietResponse getDietDetail(int dietNo);


}
