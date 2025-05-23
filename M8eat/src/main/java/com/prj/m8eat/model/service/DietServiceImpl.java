package com.prj.m8eat.model.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
//이미지 처리 및 Vision API
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.LocalizedObjectAnnotation;
import com.google.protobuf.ByteString;
import com.prj.m8eat.model.dao.DietDao;
import com.prj.m8eat.model.dao.FoodDao;
import com.prj.m8eat.model.dto.CropBox;
import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietRequest;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.Food;
import com.prj.m8eat.model.dto.FoodInfo;
//내부 유틸 클래스들 (직접 만든 클래스 기준)
import com.prj.m8eat.util.GoogleVisionUtil;
import com.prj.m8eat.util.OpenAIUtil;

@Service
public class DietServiceImpl implements DietService {

	private final DietDao dietDao;
	private final GoogleVisionUtil googleVisionUtil;
	private final OpenAIUtil openAIUtil;
	private final FoodDao foodDao;
	
	public DietServiceImpl(DietDao dietDao, GoogleVisionUtil googleVisionUtil, OpenAIUtil openAIUtil, FoodDao foodDao) {
		this.dietDao = dietDao;
		this.googleVisionUtil = googleVisionUtil;
		this.openAIUtil = openAIUtil;
		this.foodDao = foodDao;
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
        Diet oldDiet = dietDao.selectDietsByDietNo(dietReq.getDietNo());
        if (oldDiet == null) return false;

        MultipartFile newFile = dietReq.getFile();
        String newFilePath = oldDiet.getFilePath();

        if (newFile != null && !newFile.isEmpty()) {
            deleteFileIfExist(oldDiet.getFilePath());
            String fileName = UUID.randomUUID() + "_" + newFile.getOriginalFilename();
            File saveFile = new File(baseDir, fileName);
            try {
                newFile.transferTo(saveFile);
                newFilePath = "/upload/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        List<DietsFood> foodsList;
        try {
            foodsList = mapper.readValue(
                    dietReq.getFoods(),
                    new TypeReference<List<DietsFood>>() {}
            );
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Diet updateDiet = new Diet(
                dietReq.getDietNo(),
                dietReq.getUserNo(),
                dietReq.getMealType(),
                newFilePath
        );
        int result = dietDao.updateDiet(updateDiet);

        dietDao.deleteDietFood(dietReq.getDietNo());
        for (DietsFood food : foodsList) {
            food.setDietNo(dietReq.getDietNo());
            dietDao.insertDietsFood(food);
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

//	@Override
//	public List<DietResponse> getDietsByDietNo(int dietNo) {
//		List<DietResponse> dietList = new ArrayList<>();
//
////        List<Diet> diets = dietDao.selectDietsByDietNo(dietNo);
//		Diet diet = dietDao.selectDietsByDietNo(dietNo);
////        for (Diet diet : diets) {
//		DietResponse res = new DietResponse(diet.getDietNo(), diet.getUserNo(), diet.getFilePath(), diet.getRegDate(),
//				diet.getMealType());
//		List<DietsFood> dietsFood = dietDao.selectDietsFoodByDietNo(diet.getDietNo());
//		res.setFoods(dietsFood);
//		dietList.add(res);
////        }
//
//		return dietList;
//	}

//	@Override
//	public List<DietResponse> getDietsByDietNo(int dietNo) {
//	    List<DietResponse> dietList = new ArrayList<>();
//	    System.out.println("서비스에 다이어트 넘버 들어옴"+dietNo);
//	    Diet diet = dietDao.selectDietsByDietNo(dietNo);
//	    
//	    DietResponse res = new DietResponse(
//	        diet.getDietNo(),
//	        diet.getUserNo(),
//	        diet.getFilePath(),
//	        diet.getRegDate(),
//	        diet.getMealType()
//	    );
//	    res.setMealDate(diet.getMealDate()); // 🔹 여기 추가
//	    List<DietsFood> dietsFoodList = dietDao.selectDietsFoodByDietNo(diet.getDietNo());
//	    System.out.println("▶▶ 리스트 사이즈: " + dietsFoodList.size());
//	    res.setFoods(dietsFoodList);
//	    for (DietsFood df : dietsFoodList) {
//	        // food_id로 food 마스터 정보 조회 후 계산된 영양소를 설정
//	        Food masterFood = foodDao.selectFoodById(df.getFoodId());
//	        if (masterFood != null) {
//	            df.setFoodName(masterFood.getNameKo());
//	            df.setCalorie((int) Math.round(masterFood.getCalories() * (df.getAmount() / 100.0)));
//	            df.setProtein(masterFood.getProtein() * (df.getAmount() / 100.0));
//	            df.setFat(masterFood.getFat() * (df.getAmount() / 100.0));
//	            df.setCarbohydrate(masterFood.getCarbohydrate() * (df.getAmount() / 100.0));
//	            df.setSugar(masterFood.getSugar() * (df.getAmount() / 100.0));
//	            df.setCholesterol(masterFood.getCholesterol() * (df.getAmount() / 100.0));
//	        }
//	    }
//
//	    res.setFoods(dietsFoodList);
//	    System.out.println("▶▶ diets_food 리스트 사이즈: " + dietsFoodList.size());
//	    for (DietsFood df : dietsFoodList) {
//	        System.out.println("▶▶ 음식명: " + df.getFoodName() + ", g: " + df.getAmount());
//	    }
//
//	    dietList.add(res);
//
//	    return dietList;
//	}

	@Override
	public List<DietResponse> getDietsByDietNo(int dietNo) {
	    List<DietResponse> dietList = new ArrayList<>();
	    System.out.println("서비스에 다이어트 넘버 들어옴: " + dietNo);

	    Diet diet = dietDao.selectDietsByDietNo(dietNo);

	    DietResponse res = new DietResponse(
	        diet.getDietNo(),
	        diet.getUserNo(),
	        diet.getFilePath(),
	        diet.getRegDate(),
	        diet.getMealType()
	    );
	    res.setMealDate(diet.getMealDate());

	    List<DietsFood> dietsFoodList = dietDao.selectDietsFoodByDietNo(diet.getDietNo());
	    System.out.println("▶▶ diets_food 리스트 사이즈: " + dietsFoodList.size());

	    for (DietsFood df : dietsFoodList) {
	        System.out.println("▶▶ 음식명: " + df.getFoodName() + ", g: " + df.getAmount());
	    }

	    // 👉 masterFood로 계산하지 말고 그대로 사용
	    res.setFoods(dietsFoodList);
	    dietList.add(res);

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
	

    @Transactional
    @Override
    public int createDietWithFoods(Diet diet, List<DietsFood> foods) {
        dietDao.insertDiet(diet); // dietNo 생성됨
        int dietNo = diet.getDietNo();

        for (DietsFood food : foods) {
            food.setDietNo(dietNo); // 연결
            dietDao.insertDietsFood(food);
        }

        return dietNo;
    }



//    @Override
//    public DietResponse getDietDetail(int dietNo) {
//        Diet diet = dietDao.selectDietsByDietNo(dietNo);
//        if (diet == null) return null;
//
//        List<DietsFood> foodList = dietDao.selectDietsFoodByDietNo(dietNo);
//
//        DietResponse response = new DietResponse(
//            diet.getDietNo(),
//            diet.getUserNo(),
//            diet.getFilePath(),
//            diet.getRegDate(),
//            diet.getMealType()
//        );
//        response.setFoods(foodList);
//
//        return response;
//    }


}
