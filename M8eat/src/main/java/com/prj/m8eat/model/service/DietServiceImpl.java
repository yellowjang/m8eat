package com.prj.m8eat.model.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;

import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.prj.m8eat.model.dao.DietDao;
import com.prj.m8eat.model.dto.CropBox;
import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietRequest;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.Food;
import com.prj.m8eat.model.dto.FoodInfo;
import com.prj.m8eat.util.GoogleTranslateUtil;
import com.prj.m8eat.util.GoogleVisionUtil;

@Service
public class DietServiceImpl implements DietService {
	
	private final DietDao dietDao;
	public DietServiceImpl(DietDao dietDao) {
		this.dietDao = dietDao;
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

	
//	@Override
//	public List<Map<String, Object>> analyzeImageAndMatchLabels(MultipartFile file) throws Exception {
//        // Vision Client 준비
//        ImageAnnotatorClient vision = GoogleVisionUtil.createClient();
//
//        // 전체 이미지
//        BufferedImage fullImage = ImageIO.read(file.getInputStream());
//        ByteString fullBytes = ByteString.copyFrom(file.getBytes());
//
//        // DB 음식명 불러오기
//        List<String> dbNames = dietDao.getAllDietNames(); // diet.name 기준
//        JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
//
//        List<Map<String, Object>> finalResult = new ArrayList<>();
//
//        // OBJECT_LOCALIZATION
//        List<LocalizedObjectAnnotation> objects = GoogleVisionUtil.detectObjects(vision, fullBytes);
//
//        for (LocalizedObjectAnnotation obj : objects) {
//            if (!List.of("Food", "Bowl", "Plate", "Tableware").contains(obj.getName())) continue;
//
//            // 박스 자르기
//            CropBox box = GoogleVisionUtil.extractBox(obj, fullImage.getWidth(), fullImage.getHeight());
//            if (box == null) continue;
//
//            BufferedImage cropped = GoogleVisionUtil.cropImage(fullImage, box);
//            ByteString croppedBytes = GoogleVisionUtil.toByteString(cropped);
//
//            if (croppedBytes.isEmpty()) continue;
//
////            // LABEL_DETECTION
////            String label = GoogleVisionUtil.detectLabel(vision, croppedBytes);
////            if (label == null) continue;
////
////            // 매칭
////            String bestMatch = dbNames.stream()
////                    .max(Comparator.comparingDouble(name -> similarity.apply(label.toLowerCase(), name.toLowerCase())))
////                    .orElse(null);
////
////            FoodInfo matchedDiet = dietDao.getDietByName(bestMatch);
////
////            Map<String, Object> item = new HashMap<>();
////            item.put("label", label);
////            item.put("matched", bestMatch);
////            item.put("nutrition", matchedDiet);
////            item.put("box", box.toMap());
////
////            finalResult.add(item);
//         // LABEL_DETECTION
//            String label = GoogleVisionUtil.detectLabel(vision, croppedBytes);
//            if (label == null) continue;
//
//            // 🔁 번역
//            String translatedLabel = GoogleTranslateUtil.translateToKorean(label);
//            System.out.println("🔤 영어: " + label + " → 한글: " + translatedLabel);
//
//            // 유사도 기반 매칭 (한글)
//            String bestMatch = dbNames.stream()
//                    .max(Comparator.comparingDouble(name ->
//                        similarity.apply(translatedLabel.toLowerCase(), name.toLowerCase())
//                    ))
//                    .orElse(null);
//
//            // 매칭된 식단 영양 정보 조회
//            FoodInfo matchedDiet = dietDao.getDietByName(bestMatch);
//
//            // 결과 구성
//            Map<String, Object> item = new HashMap<>();
//            item.put("label", label); // 원래 라벨 (영문)
//            item.put("translated", translatedLabel); // 번역된 라벨 (한글)
//            item.put("matched", bestMatch); // DB에서 가장 유사한 음식명
//            item.put("nutrition", matchedDiet); // 영양정보
//            item.put("box", box.toMap());
//
//            finalResult.add(item);
//        }
//
//        vision.shutdown();
//        return finalResult;
//    }
	
	@Override
	public List<Map<String, Object>> analyzeImageAndMatchLabels(MultipartFile file) throws Exception {
	    // 1. Vision Client
	    ImageAnnotatorClient vision = GoogleVisionUtil.createClient();

	    // 2. 전체 이미지 로딩
	    BufferedImage fullImage = ImageIO.read(file.getInputStream());
	    ByteString fullBytes = ByteString.copyFrom(file.getBytes());

	    // 3. DB 음식명 가져오기
	    List<String> dbNames = dietDao.getAllDietNames(); // diet.name 기준
	    JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

	    List<Map<String, Object>> finalResult = new ArrayList<>();

	    // 4. OBJECT_LOCALIZATION
	    List<LocalizedObjectAnnotation> objects = GoogleVisionUtil.detectObjects(vision, fullBytes);

	    for (LocalizedObjectAnnotation obj : objects) {
	        if (!List.of("Food", "Bowl", "Plate", "Tableware").contains(obj.getName())) continue;

	        // 5. 박스 좌표 계산
	        CropBox box = GoogleVisionUtil.extractBox(obj, fullImage.getWidth(), fullImage.getHeight());
	        if (box == null) continue;

	        // ✅ 6. Padding 보정
	        int padding = 20;
	        int cropX = Math.max(0, box.getX() - padding);
	        int cropY = Math.max(0, box.getY() - padding);
	        int cropW = Math.min(fullImage.getWidth() - cropX, box.getWidth() + padding * 2);
	        int cropH = Math.min(fullImage.getHeight() - cropY, box.getHeight() + padding * 2);

	        BufferedImage cropped = fullImage.getSubimage(cropX, cropY, cropW, cropH);
	        ByteString croppedBytes = GoogleVisionUtil.toByteString(cropped);

	        if (croppedBytes.isEmpty()) continue;

	        // 7. LABEL_DETECTION 수행
	        List<EntityAnnotation> labels = GoogleVisionUtil.detectLabels(vision, croppedBytes);
	        if (labels == null || labels.isEmpty()) continue;
	        
	        System.out.println("🧠 원본 라벨 목록:");
	        for (EntityAnnotation l : labels) {
	            System.out.println(" - " + l.getDescription() + " (" + l.getScore() + ")");
	        }


	        String bestTranslatedLabel = null;
	        String bestMatchedName = null;
	        FoodInfo matchedDiet = null;
	        double maxSimilarity = 0;

	        // ✅ 8. 여러 라벨 중 최적 매칭 탐색
	        for (EntityAnnotation label : labels) {
	            String translated = GoogleTranslateUtil.translateToKorean(label.getDescription());
	            for (String name : dbNames) {
	                double score = similarity.apply(translated.toLowerCase(), name.toLowerCase());
	                if (score > maxSimilarity) {
	                    maxSimilarity = score;
	                    bestTranslatedLabel = translated;
	                    bestMatchedName = name;
	                }
	            }
	        }

	        // 유사도 임계값 검사 (ex. 0.85 이하 매칭 없음 처리)
	        if (maxSimilarity < 0.7) {
	            bestMatchedName = "매칭 없음";
	            matchedDiet = null;
	        } else {
	            matchedDiet = dietDao.getDietByName(bestMatchedName);
	        }

	        // ✅ 9. 결과 구성
	        Map<String, Object> item = new HashMap<>();
	        item.put("label", labels.get(0).getDescription()); // 가장 상위 영문 라벨
	        item.put("translated", bestTranslatedLabel);       // 가장 유사한 한글 번역
	        item.put("matched", bestMatchedName);              // DB 매칭 이름
	        item.put("nutrition", matchedDiet);                // 영양정보 (nullable)
	        item.put("box", Map.of(
	            "x", cropX,
	            "y", cropY,
	            "width", cropW,
	            "height", cropH
	        ));

	        finalResult.add(item);
	    }

	    vision.shutdown();
	    return finalResult;
	}



}
