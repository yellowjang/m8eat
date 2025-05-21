package com.prj.m8eat.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.core.io.ClassPathResource;

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

import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietRequest;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.service.DietService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/diets")
@CrossOrigin("*")
public class DietController {

	private final DietService dietService;

	public DietController(DietService dietService) {
		this.dietService = dietService;
	}

	@Value("${file.upload.dir}")
	private String baseDir;

	// 식단 전체 조회
	@GetMapping
	public ResponseEntity<?> getAllDiets() {
		List<DietResponse> dietList = dietService.getAllDiets();
		System.out.println(dietList);
		if (dietList == null || dietList.size() == 0) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(dietList);
	}

	// 유저별 식단 조회
	@GetMapping("/user/{userNo}")
	public ResponseEntity<?> getDietsByUserNo(@PathVariable int userNo) {
		List<DietResponse> dietList = dietService.getDietsByUserNo(userNo);
		if (dietList == null || dietList.size() == 0) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(dietList);
	}

	// 날짜별 식단 조회
	@GetMapping("/date")
	public ResponseEntity<?> getDietsByDate(@RequestParam("start") String startDate,
			@RequestParam("end") String endDate) {
//		System.out.println(startDate + " " + endDate);
		List<DietResponse> dietList = dietService.getDietsByDate(startDate, endDate);
		if (dietList == null || dietList.size() == 0) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(dietList);
	}

	// 식단 상세 조회
	@GetMapping("/{dietNo}")
	public ResponseEntity<?> getDietsByDietNo(@PathVariable int dietNo) {
//		System.out.println(startDate + " " + endDate);
		List<DietResponse> dietList = dietService.getDietsByDietNo(dietNo);
		if (dietList == null || dietList.size() == 0) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(dietList);
	}

	// 식단 등록
	@PostMapping
	public ResponseEntity<String> writeDiets(@ModelAttribute DietRequest dietReq, HttpSession session) {

//		User loginUser = (User) session.getAttribute("loginUser");

		Diet diet = new Diet();
//		diet.setUserNo(loginUser.getUserNo());
		diet.setUserNo(3);
		System.out.println(diet);
		diet.setMealType(dietReq.getMealType());

		MultipartFile file = dietReq.getFile();

		if (file != null && !file.isEmpty()) {
			String originalFilename = file.getOriginalFilename();
//			String uploadDirPath = "/Users/jang-ayoung/Desktop/m8eat/data"; // 수정수정수정
//			String uploadDirPath = "C:\\SSAFY\\m8eat"; // 수정수정수정
//			String uploadDirPath = "C:\\Users\\kmj\\Desktop\\SSAFY\\m8eat\\data"; // 수정수정수정

			String uploadDirPath = baseDir; // 수정수정수정

			File uploadDir = new File(uploadDirPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}

			try {
				File saveFile = new File(uploadDir, originalFilename);
				file.transferTo(saveFile);
				diet.setFilePath("/upload/" + originalFilename);
				System.out.println("controller " + diet);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
			}
		}

		if (dietService.writeDiets(diet, dietReq.getFoods())) {
			return ResponseEntity.ok("식단이 성공적으로 등록되었습니다.");
		}
		;

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("식단 등록에 실패했습니다");
	}

	// 식단 삭제
	@DeleteMapping("/{dietNo}")
	public ResponseEntity<String> deleteDietByDietNo(@PathVariable int dietNo) {
		if (dietService.deleteDietByDietNo(dietNo)) {
			return ResponseEntity.ok("삭제되었습니다.");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("요청을 처리하지 못했습니다.");
	}

	// 식단 수정
	@PutMapping("/{dietNo}")
	public ResponseEntity<String> updateDietByDietNo(@PathVariable int dietNo, @ModelAttribute DietRequest dietReq) {
		dietReq.setDietNo(dietNo);
		System.out.println("updatediets controllerrrrrrrrrr " + dietReq);
		if (dietService.updateDietByDietNo(dietReq)) {
			return ResponseEntity.ok("정상적으로 수정되었습니다.");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("요청을 처리하지 못했습니다.");
	}


//	@PostMapping("/api/image/label")
//	public ResponseEntity<?> detectLabels(@RequestParam("file") MultipartFile file) {
//		try {
//			// ✅ 자격 증명 파일 직접 읽어오기
//			GoogleCredentials credentials = GoogleCredentials
//					.fromStream(new ClassPathResource(
//							"braided-gravity-460410-r6-1e7c33f2eb8a.json").getInputStream())
//					.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//			// ✅ 자격 증명 기반으로 클라이언트 생성
//			ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
//					.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
//
//			ImageAnnotatorClient vision = ImageAnnotatorClient.create(settings);
//
//			// 이미지 바이트 변환
//			ByteString imgBytes = ByteString.readFrom(file.getInputStream());
//			Image img = Image.newBuilder().setContent(imgBytes).build();
//			Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
//			AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
//
//			List<AnnotateImageRequest> requests = List.of(request);
//			BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
//
//			// 결과 파싱
//			List<Map<String, Object>> result = new ArrayList<>();
//			for (EntityAnnotation label : response.getResponses(0).getLabelAnnotationsList()) {
//				Map<String, Object> labelInfo = new HashMap<>();
//				labelInfo.put("description", label.getDescription());
//				labelInfo.put("score", label.getScore());
//				result.add(labelInfo);
//			}
//
//			return ResponseEntity.ok(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("분석 실패: " + e.getMessage());
//		}
//	}
//	@PostMapping("/api/image/object")
//	@PostMapping("/api/image/label")
//	public ResponseEntity<?> detectObjects(@RequestParam("file") MultipartFile file) {
//	    try {
//	        // ✅ 자격 증명
//	        GoogleCredentials credentials = GoogleCredentials
//	                .fromStream(new ClassPathResource("braided-gravity-460410-r6-1e7c33f2eb8a.json").getInputStream())
//	                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//	        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
//	                .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
//
//	        ImageAnnotatorClient vision = ImageAnnotatorClient.create(settings);
//
//	        // ✅ 이미지 구성
//	        ByteString imgBytes = ByteString.readFrom(file.getInputStream());
//	        Image img = Image.newBuilder().setContent(imgBytes).build();
//	        Feature feat = Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION).build();
//
//	        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
//	                .addFeatures(feat)
//	                .setImage(img)
//	                .build();
//
//	        List<AnnotateImageRequest> requests = List.of(request);
//	        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
//
//	        // ✅ 결과 파싱
//	        List<Map<String, Object>> result = new ArrayList<>();
//	        for (LocalizedObjectAnnotation obj : response.getResponses(0).getLocalizedObjectAnnotationsList()) {
//	            Map<String, Object> objectInfo = new HashMap<>();
//	            objectInfo.put("name", obj.getName());
//	            objectInfo.put("score", obj.getScore());
//
//	            // ✅ 좌표만 따로 정리해서 담기 (Jackson이 직렬화할 수 있는 타입만)
//	            List<Map<String, Float>> vertices = new ArrayList<>();
//	            for (NormalizedVertex v : obj.getBoundingPoly().getNormalizedVerticesList()) {
//	                Map<String, Float> point = new HashMap<>();
//	                point.put("x", v.getX());
//	                point.put("y", v.getY());
//	                vertices.add(point);
//	            }
//	            objectInfo.put("vertices", vertices); // <- 이 부분만 넣기
//
//	            result.add(objectInfo);
//	        }
//
////	        for (LocalizedObjectAnnotation obj : response.getResponses(0).getLocalizedObjectAnnotationsList()) {
////	            Map<String, Object> objectInfo = new HashMap<>();
////	            objectInfo.put("name", obj.getName());         // ex. "Egg"
////	            objectInfo.put("score", obj.getScore());       // 신뢰도
////	            objectInfo.put("boundingPoly", obj.getBoundingPoly()); // 위치 정보
////
////	            // bounding box 정규화된 좌표만 정리
////	            List<Map<String, Float>> vertices = new ArrayList<>();
////	            for (NormalizedVertex v : obj.getBoundingPoly().getNormalizedVerticesList()) {
////	                Map<String, Float> point = new HashMap<>();
////	                point.put("x", v.getX());
////	                point.put("y", v.getY());
////	                vertices.add(point);
////	            }
////	            objectInfo.put("vertices", vertices);
////
////	            result.add(objectInfo);
////	        }
//
//	        return ResponseEntity.ok(result);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("분석 실패: " + e.getMessage());
//	    }
//	}

//	@PostMapping("/api/image/label")
//	public ResponseEntity<?> detectCroppedFoodLabels(@RequestParam("file") MultipartFile file) {
//	    try {
//	        // 🔹 이미지 바이트 전체 읽기 (for reuse)
//	        byte[] rawBytes = file.getBytes();
//	        BufferedImage fullImage = ImageIO.read(new ByteArrayInputStream(rawBytes));
//	        ByteString fullBytes = ByteString.copyFrom(rawBytes);
//
//	        // 🔹 Vision API 자격 설정
//	        GoogleCredentials credentials = GoogleCredentials
//	            .fromStream(new ClassPathResource("braided-gravity-460410-r6-1e7c33f2eb8a.json").getInputStream())
//	            .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//	        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
//	            .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
//
//	        List<Map<String, Object>> finalResult = new ArrayList<>();
//
//	        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(settings)) {
//
//	            // 🔸 객체 인식 요청
//	            Image fullImg = Image.newBuilder().setContent(fullBytes).build();
//	            Feature objectFeature = Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION).build();
//	            AnnotateImageRequest objectRequest = AnnotateImageRequest.newBuilder()
//	                .addFeatures(objectFeature).setImage(fullImg).build();
//
//	            BatchAnnotateImagesResponse objectResponse = vision.batchAnnotateImages(List.of(objectRequest));
//	            List<LocalizedObjectAnnotation> objects = objectResponse.getResponses(0).getLocalizedObjectAnnotationsList();
//
//	            // 🔸 DB 음식 이름 전부 불러오기
//	            List<String> dbNames = foodMapper.getAllFoodNames(); // name 컬럼 (한글)
//
//	            JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
//
//	            for (LocalizedObjectAnnotation obj : objects) {
//	                if (!List.of("Food", "Tableware", "Bowl", "Plate").contains(obj.getName())) continue;
//
//	                // ✅ 좌표 계산
//	                List<NormalizedVertex> verts = obj.getBoundingPoly().getNormalizedVerticesList();
//	                float xMin = verts.stream().map(NormalizedVertex::getX).min(Float::compare).orElse(0f);
//	                float yMin = verts.stream().map(NormalizedVertex::getY).min(Float::compare).orElse(0f);
//	                float xMax = verts.stream().map(NormalizedVertex::getX).max(Float::compare).orElse(0f);
//	                float yMax = verts.stream().map(NormalizedVertex::getY).max(Float::compare).orElse(0f);
//
//	                int x = (int) (xMin * fullImage.getWidth());
//	                int y = (int) (yMin * fullImage.getHeight());
//	                int width = (int) ((xMax - xMin) * fullImage.getWidth());
//	                int height = (int) ((yMax - yMin) * fullImage.getHeight());
//
//	                if (width <= 0 || height <= 0 || x < 0 || y < 0 ||
//	                        x + width > fullImage.getWidth() || y + height > fullImage.getHeight()) {
//	                    continue;
//	                }
//
//	                // ✅ 잘라내기
//	                BufferedImage originalCrop = fullImage.getSubimage(x, y, width, height);
//	                BufferedImage cropped = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//	                Graphics2D g = cropped.createGraphics();
//	                g.drawImage(originalCrop, 0, 0, null);
//	                g.dispose();
//
//	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	                ImageIO.write(cropped, "jpg", baos);
//	                ByteString croppedBytes = ByteString.copyFrom(baos.toByteArray());
//
//	                if (croppedBytes.isEmpty()) continue;
//
//	                // ✅ 라벨 디텍션 수행
//	                Image cropImage = Image.newBuilder().setContent(croppedBytes).build();
//	                Feature labelFeature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
//	                AnnotateImageRequest labelRequest = AnnotateImageRequest.newBuilder()
//	                    .addFeatures(labelFeature).setImage(cropImage).build();
//
//	                BatchAnnotateImagesResponse labelResponse = vision.batchAnnotateImages(List.of(labelRequest));
//	                List<EntityAnnotation> labels = labelResponse.getResponses(0).getLabelAnnotationsList();
//
//	                if (!labels.isEmpty()) {
//	                    String label = labels.get(0).getDescription();
//	                    float score = labels.get(0).getScore();
//
//	                    // ✅ DB에서 가장 유사한 음식명 찾기
//	                    String bestMatch = dbNames.stream()
//	                        .max(Comparator.comparingDouble(name ->
//	                            similarity.apply(label.toLowerCase(), name.toLowerCase())))
//	                        .orElse(null);
//
//	                    // ✅ 해당 음식 영양 정보 가져오기
//	                    Food matchedFood = foodMapper.getFoodByName(bestMatch);
//
//	                    Map<String, Object> item = new HashMap<>();
//	                    item.put("label", label);
//	                    item.put("score", score);
//	                    item.put("matched", bestMatch);
//	                    item.put("nutrition", matchedFood);
//	                    item.put("box", Map.of("x", x, "y", y, "width", width, "height", height));
//
//	                    finalResult.add(item);
//	                }
//	            }
//	        }
//
//	        return ResponseEntity.ok(finalResult);
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body("분석 실패: " + e.getMessage());
//	    }
//	}

	@PostMapping("/api/image/label")
	public ResponseEntity<?> detectDietLabels(@RequestParam("file") MultipartFile file) {
	    try {
	        List<Map<String, Object>> result = dietService.analyzeImageAndMatchLabels(file);
	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("분석 실패: " + e.getMessage());
	    }
	}




}
