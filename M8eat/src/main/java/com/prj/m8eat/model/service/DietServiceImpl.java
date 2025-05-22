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
	public List<DietResponse> getDietsByUserNo(int userNo) {
		List<DietResponse> dietList = new ArrayList<>();

		List<Diet> diets = dietDao.selectDietsByUserNo(userNo);
		for (Diet diet : diets) {
			DietResponse res = new DietResponse(diet.getDietNo(), diet.getUserNo(), diet.getFilePath(), diet.getRegDate(), diet.getMealType());
			res.setFoods(new ArrayList<>());
			
			List<DietsFood> dietsFood = dietDao.selectDietsFoodByDietNo(diet.getDietNo());
			for (DietsFood food : dietsFood) {
				Food tmp = new Food(food.getFoodName(), food.getCalorie());
				res.getFoods().add(tmp);
			}
			dietList.add(res);
		}
		
		return dietList;
	}
	
	@Override
	public List<DietResponse> getDietsByDate(String startDate, String endDate) {
		List<DietResponse> dietList = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("startDate", startDate);
		map.put("endDate", endDate + " 23:59:59");
		
		List<Diet> diets = dietDao.selectDietsByDate(map);
		for (Diet diet : diets) {
			DietResponse res = new DietResponse(diet.getDietNo(), diet.getUserNo(), diet.getFilePath(), diet.getRegDate(), diet.getMealType());;
			res.setFoods(new ArrayList<>());
			dietList.add(res);
			
			List<DietsFood> dietsFood = dietDao.selectDietsFoodByDietNo(diet.getDietNo());
			for (DietsFood food : dietsFood) {
				Food tmp = new Food(food.getFoodName(), food.getCalorie());
				res.getFoods().add(tmp);
			}
		}

		return dietList;
	}
	
	@Override
	public List<DietResponse> getDietsByDietNo(int dietNo) {
		List<DietResponse> dietList = new ArrayList<>();
		
		Diet diet = dietDao.selectDietsByDietNo(dietNo);
//		for (Diet diet : diets) {
			DietResponse res = new DietResponse(diet.getDietNo(), diet.getUserNo(), diet.getFilePath(), diet.getRegDate(), diet.getMealType());
			res.setFoods(new ArrayList<>());
			
			List<DietsFood> dietsFood = dietDao.selectDietsFoodByDietNo(diet.getDietNo());
			for (DietsFood food : dietsFood) {
				res.getFoods().add(new Food(food.getFoodName(), food.getCalorie()));
			}
			
			dietList.add(res);
//		}
		
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
		for (Food f : dietReq.getFoods()) {
			DietsFood dietsFood = new DietsFood(dietReq.getDietNo(), f.getFoodName(), f.getCalorie());
			System.out.println("updateeeeeeeeeeeeeee " + dietsFood.getDietNo());
			dietDao.insertDietsFood(dietsFood);
		}
		
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
	        if (!(name.contains("food") || name.contains("dish") || name.contains("meal"))) continue;

	        CropBox box = GoogleVisionUtil.extractBox(obj, fullImage.getWidth(), fullImage.getHeight());
	        if (box == null) continue;

	        int x = Math.max(0, box.getX() - 20);
	        int y = Math.max(0, box.getY() - 20);
	        int w = Math.min(fullImage.getWidth() - x, box.getWidth() + 40);
	        int h = Math.min(fullImage.getHeight() - y, box.getHeight() + 40);

	        BufferedImage cropped = fullImage.getSubimage(x, y, w, h);
	        ByteString croppedBytes = GoogleVisionUtil.toByteString(cropped);
	        if (croppedBytes.isEmpty()) continue;

	        // 4. Label Detection (Top 3, 일반적인 단어 제외)
	        List<EntityAnnotation> labels = googleVisionUtil.detectLabels(vision, croppedBytes);
	        if (labels == null || labels.isEmpty()) continue;

	        List<String> filtered = labels.stream()
	            .map(EntityAnnotation::getDescription)
	            .filter(desc -> {
	                String d = desc.toLowerCase();
	                return !(d.contains("food") || d.contains("dish") || d.contains("ingredient")
	                		|| d.contains("tableware")|| d.contains("cookware")|| d.contains("cooking")|| d.contains("recipe"));
	            })
	            .limit(3)
	            .toList();

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
	        if (maxScore < 0.5) bestMatch = "매칭 없음";

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


//	@Override
//	public List<Map<String, Object>> analyzeImageWithVisionAndGpt(MultipartFile file) throws Exception {
//	    List<Map<String, Object>> results = new ArrayList<>();
//
//	    // 1. Vision API 클라이언트 생성
//	    ImageAnnotatorClient vision = googleVisionUtil.createClient();
//	    System.out.println("1️⃣ Vision client 연결 완료");
//
//	    // 2. 전체 이미지 로딩
//	    BufferedImage fullImage = ImageIO.read(file.getInputStream());
//	    ByteString fullBytes = ByteString.copyFrom(file.getBytes());
//
//	    // 3. DB 음식 이름 목록 불러오기
//	    List<String> dbNames = dietDao.getAllDietNames();
//	    JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
//
//	    // 4. Object Detection 수행
//	    List<LocalizedObjectAnnotation> objects = googleVisionUtil.detectObjects(vision, fullBytes);
//	    System.out.println("2️⃣ Object Detection 결과: " + objects.size());
//
//	    for (LocalizedObjectAnnotation obj : objects) {
//	        String objName = obj.getName().toLowerCase();
//	        System.out.println("🎯 감지된 객체: " + objName);
//
//	        // ✅ 조건 완화: 일단 모든 객체 대상으로 테스트 (원래는 food/dish/meal만 통과)
//	        // if (!(objName.contains("food") || objName.contains("dish") || objName.contains("meal"))) continue;
//
//	        // 5. 박스 추출
//	        CropBox box = GoogleVisionUtil.extractBox(obj, fullImage.getWidth(), fullImage.getHeight());
//	        System.out.println("boxx " + box.toString());
//	        if (box == null) continue;
//
//	        // 6. 박스 크롭 + padding
//	        int padding = 20;
//	        int x = Math.max(0, box.getX() - padding);
//	        int y = Math.max(0, box.getY() - padding);
//	        int w = Math.min(fullImage.getWidth() - x, box.getWidth() + padding * 2);
//	        int h = Math.min(fullImage.getHeight() - y, box.getHeight() + padding * 2);
//
//	        BufferedImage cropped = fullImage.getSubimage(x, y, w, h);
//	        ByteString croppedBytes = googleVisionUtil.toByteString(cropped);
//	        System.out.println("croppedBytes " + croppedBytes);
//	        if (croppedBytes.isEmpty()) continue;
//
//	        // 7. Label Detection
//	        
//	        System.out.println("🖼️ 크롭 좌표: x=" + x + ", y=" + y + ", w=" + w + ", h=" + h);
//	        System.out.println("🧪 크롭된 이미지 바이트 크기: " + croppedBytes.size());
//	        
//	        List<EntityAnnotation> labels = googleVisionUtil.detectLabels(vision, croppedBytes);
////	        System.out.println("📦 Label 개수: " + (labels != null ? labels.size() : "null"));
////	        
////	        List<EntityAnnotation> labels = googleVisionUtil.detectLabels(vision, croppedBytes);
//	        if (labels == null || labels.isEmpty()) {
//	            System.out.println("❌ 라벨 없음 or 감지 실패");
//	            continue;
//	        }
//
//	        System.out.println("✅ 라벨 탐지 성공");
//	        System.out.println("🔖 가장 높은 라벨: " + labels.get(0).getDescription());
//
////	        if (labels != null) {
////	            for (EntityAnnotation label : labels) {
////	                System.out.println("🧾 라벨: " + label.getDescription() + " (" + label.getScore() + ")");
////	            }
////	        }
////
////	        if (labels == null || labels.isEmpty()) continue;
//
//	        String engLabel = labels.get(0).getDescription();
//	        System.out.println("3️⃣ Label Detection 완료 → " + engLabel);
//
//	        // 8. GPT로 한글 메뉴 보정
//	        String koreanLabel = openAIUtil.gptTranslateMenuName(engLabel);
//	        System.out.println("🌐 번역결과: " + koreanLabel);
//
//	        // 9. DB 매칭
//	        String bestMatch = null;
//	        double maxSim = 0;
//	        for (String name : dbNames) {
//	            double score = similarity.apply(koreanLabel.toLowerCase(), name.toLowerCase());
//	            if (score > maxSim) {
//	                maxSim = score;
//	                bestMatch = name;
//	            }
//	        }
//	        if (maxSim < 0.7) {
//	            bestMatch = "매칭 없음";
//	        }
//
//	        FoodInfo matched = null;
//	        if (!"매칭 없음".equals(bestMatch)) {
//	            matched = dietDao.getDietByName(bestMatch);
//	        }
//
//	        // 10. 결과 구성
//	        Map<String, Object> item = new HashMap<>();
//	        item.put("label", engLabel);
//	        item.put("translated", koreanLabel);
//	        item.put("matched", bestMatch);
//	        item.put("nutrition", matched);
//	        item.put("box", Map.of("x", x, "y", y, "width", w, "height", h));
//
//	        results.add(item);
//	    }
//
//	    vision.shutdown();
//	    System.out.println("✅ 최종 결과 개수: " + results.size());
//	    return results;
//	}





}
