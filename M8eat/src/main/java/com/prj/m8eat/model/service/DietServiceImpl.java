package com.prj.m8eat.model.service;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.prj.m8eat.model.dao.DietDao;
import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietRequest;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.Food;
import com.prj.m8eat.model.dto.FoodInfo;

//이미지 처리 및 Vision API
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.LocalizedObjectAnnotation;
import com.google.protobuf.ByteString;

import java.io.ByteArrayOutputStream;

//JSON 처리
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//내부 유틸 클래스들 (직접 만든 클래스 기준)
import com.prj.m8eat.util.GoogleVisionUtil;
import com.prj.m8eat.util.OpenAIUtil;
import com.prj.m8eat.model.dto.CropBox;

@Service
public class DietServiceImpl implements DietService {

	private final DietDao dietDao;
	private final GoogleVisionUtil googleVisionUtil;
	private final OpenAIUtil openAIUtil;

	public DietServiceImpl(DietDao dietDao, GoogleVisionUtil googleVisionUtil, OpenAIUtil openAIUtil) {
		this.dietDao = dietDao;
		this.googleVisionUtil = googleVisionUtil;
		this.openAIUtil = openAIUtil;
	}

	@Value("${file.upload.dir}")
	private String baseDir;

//	@Override
//	public List<DietResponse> getAllDiets() {
//		List<DietResponse> dietList = new ArrayList<>();
//		Map<Integer, DietResponse> dietMap = new HashMap<>();

	@Override
	public List<DietResponse> getAllDiets() {
		List<DietResponse> dietList = new ArrayList<>();
		Map<Integer, DietResponse> dietMap = new HashMap<>();

		List<Diet> diets = dietDao.selectAllDiets();
		for (Diet diet : diets) {
			DietResponse res = new DietResponse(diet.getDietNo(), diet.getUserNo(), diet.getFilePath(),
					diet.getRegDate(), diet.getMealType());
			res.setFoods(new ArrayList<>());
			dietList.add(res);
			dietMap.put(diet.getDietNo(), res);
		}

		List<DietsFood> dietsFood = dietDao.selectAllDietsFood();
		for (DietsFood food : dietsFood) {
			DietResponse target = dietMap.get(food.getDietNo());
			if (target != null) {
				target.getFoods().add(food);
			}
		}
		return dietList;
	}

	@Override
	public List<DietResponse> getDietsByUserNo(int userNo) {
		List<DietResponse> dietList = new ArrayList<>();

		List<Diet> diets = dietDao.selectDietsByUserNo(userNo);
		for (Diet diet : diets) {
			DietResponse res = new DietResponse(diet.getDietNo(), diet.getUserNo(), diet.getFilePath(),
					diet.getRegDate(), diet.getMealType());
			List<DietsFood> dietsFood = dietDao.selectDietsFoodByDietNo(diet.getDietNo());
			res.setFoods(dietsFood);
			dietList.add(res);
		}
		return dietList;
	}

	@Override
	public boolean deleteDietByDietNo(int dietNo) {
		if (dietDao.deleteDiet(dietNo) > 0) {
			return true;
		}
		return false;
	}


	@Override
	public boolean updateDietByDietNo(DietRequest dietReq) {
		
		System.out.println("ssssssssssssss");
		
		Diet oldDiet = dietDao.selectDietsByDietNo(dietReq.getDietNo());
//		System.out.println("serviceeee " + oldData.getFilePath());
		if (oldDiet == null) return false;
		
		MultipartFile newFile = dietReq.getFile();
		
		// 파일이 새로 업로드 된 경우
		if (newFile != null && newFile.isEmpty()) {
			deleteFileIfExist(oldDiet.getFilePath());
			
			String fileName = UUID.randomUUID() + "_" + newFile.getOriginalFilename(); 
			File saveFile = new File(baseDir, fileName);
			
			try {
				newFile.transferTo(saveFile);
				String newFilePath = "/upload/" + fileName;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		System.out.println("updateeeeeeeeeeeeeee " + dietReq.getDietNo());
		Diet updateDiet = new Diet(dietReq.getDietNo(), dietReq.getUserNo(),
									dietReq.getMealType(), dietReq.getFilePath());
		int result = dietDao.updateDiet(updateDiet);
		
		dietDao.deleteDietFood(dietReq.getDietNo());
//		for (Food f : dietReq.getFoods()) {
//			DietsFood dietsFood = new DietsFood(dietReq.getDietNo(), f.getFoodName(), f.getCalorie());
//			System.out.println("updateeeeeeeeeeeeeee " + dietsFood.getDietNo());
//			dietDao.insertDietsFood(dietsFood);
//		}
		
		return result > 0;
	}

	private void deleteFileIfExist(String filePath) {
		if (filePath == null || filePath.isEmpty()) return;
		
		String fileName = filePath.replace("/upload", "");
		File file = new File(baseDir, fileName);
		
		if (file.exists()) file.delete();
	}
	
	@Override
	public List<Map<String, Object>> analyzeImageWithVisionAndGpt(MultipartFile file) throws Exception {
		List<Map<String, Object>> results = new ArrayList<>();

		// 1. Vision API 클라이언트 생성
		ImageAnnotatorClient vision = googleVisionUtil.createClient();
		BufferedImage fullImage = ImageIO.read(file.getInputStream());
		ByteString fullBytes = ByteString.copyFrom(file.getBytes());

		// 2. 음식 이름 목록
		List<String> dbNames = dietDao.getAllDietNames();
		JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

		// 3. Object Detection
		List<LocalizedObjectAnnotation> objects = googleVisionUtil.detectObjects(vision, fullBytes);
		for (LocalizedObjectAnnotation obj : objects) {
			String name = obj.getName().toLowerCase();
			if (!(name.contains("food") || name.contains("dish") || name.contains("meal")))
				continue;

			CropBox box = GoogleVisionUtil.extractBox(obj, fullImage.getWidth(), fullImage.getHeight());
			if (box == null)
				continue;

			int x = Math.max(0, box.getX() - 20);
			int y = Math.max(0, box.getY() - 20);
			int w = Math.min(fullImage.getWidth() - x, box.getWidth() + 40);
			int h = Math.min(fullImage.getHeight() - y, box.getHeight() + 40);

			BufferedImage cropped = fullImage.getSubimage(x, y, w, h);
			ByteString croppedBytes = GoogleVisionUtil.toByteString(cropped);
			if (croppedBytes.isEmpty())
				continue;

			// 4. Label Detection (Top 3, 일반적인 단어 제외)
			List<EntityAnnotation> labels = googleVisionUtil.detectLabels(vision, croppedBytes);
			if (labels == null || labels.isEmpty())
				continue;

			List<String> filtered = labels.stream().map(EntityAnnotation::getDescription).filter(desc -> {
				String d = desc.toLowerCase();
				return !(d.contains("food") || d.contains("dish") || d.contains("ingredient") || d.contains("tableware")
						|| d.contains("cookware") || d.contains("cooking") || d.contains("recipe"));
			}).limit(3).toList();

			List<String> labelList = filtered.isEmpty()
					? labels.stream().limit(3).map(EntityAnnotation::getDescription).toList()
					: filtered;

			// 5. GPT 한글 라벨 변환
//	        String prompt = "다음 음식 관련 단어들을 보고 자연스러운 한국 음식 이름을 하나 지어줘: " + String.join(", ", labelList);
			String prompt = "다음 음식 관련 단어들을 보고 어떤 음식일지 한국어로 알려줘: " + String.join(", ", labelList);
			String koreanLabel = openAIUtil.gptTranslateMenuName(prompt);

			// 6. DB 매칭
			String bestMatch = null;
			double maxScore = 0;
			for (String candidate : dbNames) {
				double score = similarity.apply(koreanLabel.toLowerCase(), candidate.toLowerCase());
				if (score > maxScore) {
					maxScore = score;
					bestMatch = candidate;
				}
			}
			if (maxScore < 0.5)
				bestMatch = "매칭 없음";

			FoodInfo nutrition = null;
			if (!"매칭 없음".equals(bestMatch)) {
				nutrition = dietDao.getDietByName(bestMatch);
			}

			Map<String, Object> result = new HashMap<>();
			result.put("label", labelList); // <- join() 하지 말고 리스트 자체로 전달
			result.put("translated", koreanLabel);
			result.put("matched", bestMatch);
			result.put("nutrition", nutrition);
			result.put("box", Map.of("x", x, "y", y, "width", w, "height", h));

			results.add(result);
		}

		vision.shutdown();
		return results;
	}

	@Override
	public List<DietResponse> getDietsByDate(String startDate, String endDate) {
		List<DietResponse> dietList = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("startDate", startDate);
		map.put("endDate", endDate + " 23:59:59");

		List<Diet> diets = dietDao.selectDietsByDate(map);
		for (Diet diet : diets) {
			DietResponse res = new DietResponse(diet.getDietNo(), diet.getUserNo(), diet.getFilePath(),
					diet.getRegDate(), diet.getMealType());
			List<DietsFood> dietsFood = dietDao.selectDietsFoodByDietNo(diet.getDietNo());
			res.setFoods(dietsFood);
			dietList.add(res);
		}

		return dietList;
	}

	@Override
	public List<DietResponse> getDietsByDietNo(int dietNo) {
		List<DietResponse> dietList = new ArrayList<>();

//        List<Diet> diets = dietDao.selectDietsByDietNo(dietNo);
		Diet diet = dietDao.selectDietsByDietNo(dietNo);
//        for (Diet diet : diets) {
		DietResponse res = new DietResponse(diet.getDietNo(), diet.getUserNo(), diet.getFilePath(), diet.getRegDate(),
				diet.getMealType());
		List<DietsFood> dietsFood = dietDao.selectDietsFoodByDietNo(diet.getDietNo());
		res.setFoods(dietsFood);
		dietList.add(res);
//        }

		return dietList;
	}

	@Override
	public boolean writeDiets(Diet diet, List<DietsFood> inputFoods) {
		if (dietDao.insertDiet(diet) != 1)
			return false;

		for (DietsFood input : inputFoods) {
			input.setDietNo(diet.getDietNo()); // diet_no 연결
			dietDao.insertDietsFood(input); // 그대로 insert
		}

		return true;
	}


}
