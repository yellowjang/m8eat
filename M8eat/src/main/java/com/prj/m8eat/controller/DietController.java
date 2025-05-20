package com.prj.m8eat.controller;

import java.io.*;
//import java.io.IOException;
import java.util.*;
//import java.util.Map;

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

//    @PostMapping("/api/image/label")
//    public ResponseEntity<?> detectLabels(@RequestParam("file") MultipartFile file) {
//        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
//
//            // 이미지 바이트 변환
//            ByteString imgBytes = ByteString.readFrom(file.getInputStream());
//
//            // 요청 구성
//            Image img = Image.newBuilder().setContent(imgBytes).build();
//            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
//            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
//                    .addFeatures(feat)
//                    .setImage(img)
//                    .build();
//
//            // Vision API 호출
//            List<AnnotateImageRequest> requests = List.of(request);
//            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
//
//            // 결과 파싱
//            List<Map<String, Object>> result = new ArrayList<>();
//            for (EntityAnnotation label : response.getResponses(0).getLabelAnnotationsList()) {
//                Map<String, Object> labelInfo = new HashMap<>();
//                labelInfo.put("description", label.getDescription());
//                labelInfo.put("score", label.getScore());
//                result.add(labelInfo);
//            }
//
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("분석 실패: " + e.getMessage());
//        }
//    }

	@PostMapping("/api/image/label")
	public ResponseEntity<?> detectLabels(@RequestParam("file") MultipartFile file) {
		try {
			// ✅ 자격 증명 파일 직접 읽어오기
			GoogleCredentials credentials = GoogleCredentials
					.fromStream(new FileInputStream(
							"C:/Users/kmj/Desktop/SSAFY/m8eat/braided-gravity-460410-r6-1e7c33f2eb8a.json"))
					.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

			// ✅ 자격 증명 기반으로 클라이언트 생성
			ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();

			ImageAnnotatorClient vision = ImageAnnotatorClient.create(settings);

			// 이미지 바이트 변환
			ByteString imgBytes = ByteString.readFrom(file.getInputStream());
			Image img = Image.newBuilder().setContent(imgBytes).build();
			Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
			AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

			List<AnnotateImageRequest> requests = List.of(request);
			BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);

			// 결과 파싱
			List<Map<String, Object>> result = new ArrayList<>();
			for (EntityAnnotation label : response.getResponses(0).getLabelAnnotationsList()) {
				Map<String, Object> labelInfo = new HashMap<>();
				labelInfo.put("description", label.getDescription());
				labelInfo.put("score", label.getScore());
				result.add(labelInfo);
			}

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("분석 실패: " + e.getMessage());
		}
	}

}
