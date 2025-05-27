package com.prj.m8eat.controller;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj.m8eat.jwt.JwtUtil;
import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietRequest;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.service.DietService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/diets")
//@CrossOrigin("*")
public class DietController {

	private final DietService dietService;
	private final JwtUtil util;

	@Value("${file.upload.dir}")
	private String baseDir;


	public JwtUtil getUtil() {
		return util;
	}

	public DietController(DietService dietService, JwtUtil util) {
		this.dietService = dietService;
		this.util = util;
	}
	

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
//	@GetMapping("/{dietNo}")
//	public ResponseEntity<?> getDietsByDietNo(@PathVariable int dietNo) {
////		System.out.println(startDate + " " + endDate);
//		List<DietResponse> dietList = dietService.getDietsByDietNo(dietNo);
//		if (dietList == null || dietList.size() == 0) {
//			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
//		}
//		return ResponseEntity.ok(dietList);
//	}
//	
	@GetMapping("/{dietNo}")
	public ResponseEntity<List<DietResponse>> getDietDetail(@PathVariable int dietNo) {

	    List<DietResponse> detail = dietService.getDietsByDietNo(dietNo);
	    return detail != null ?
	        ResponseEntity.ok(detail) :
	        ResponseEntity.notFound().build();
	}
	// 식단 등록 
	@PostMapping
	public ResponseEntity<String> writeDiets(@ModelAttribute DietRequest dietReq, @CookieValue("access-token") String token) {
		
		int userNo;
	    if (util.validate(token)) {
	        Claims claims = util.getClaims(token);
	        userNo = (int) claims.get("userNo");
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
		
		Diet diet = new Diet();
	    diet.setUserNo(userNo); 
	    diet.setMealType(dietReq.getMealType());
	    
	    System.out.println("dietttt writeeeee" + dietReq);

	    try {
	    	System.out.println("✅ 받은 날짜: " + dietReq.getMealDate());
	    	diet.setMealDate(dietReq.getMealDate());
	        
	    } catch (DateTimeParseException e) {
	        return ResponseEntity.badRequest().body("날짜 형식 오류: " + e.getMessage());
	    }

	    // 이미지 파일 저장 처리
	    MultipartFile file = dietReq.getFile();
	    if (file != null && !file.isEmpty()) {
	    	String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
	    	File uploadDir = new File(baseDir);
	    	if (!uploadDir.exists()) uploadDir.mkdirs();
	        try {
	        	File saveFile = new File(uploadDir, fileName);
	        	file.transferTo(saveFile);
	        	diet.setFilePath("/upload/" + fileName);  // URL 경로
	        } catch (IOException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
	        }
	    }

	    // 음식 리스트 파싱
	    ObjectMapper objectMapper = new ObjectMapper();
	    List<DietsFood> foodList;
	    try {
	        foodList = objectMapper.readValue(dietReq.getFoods(), new TypeReference<List<DietsFood>>() {});
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("음식 정보 파싱 실패: " + e.getMessage());
	    }

	    // 트랜잭션 서비스 호출
	    try {
	        int dietNo = dietService.createDietWithFoods(diet, foodList);
	        return ResponseEntity.ok("식단이 성공적으로 등록되었습니다. (dietNo=" + dietNo + ")");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("식단 등록에 실패했습니다: " + e.getMessage());
	    }
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
		if (dietService.updateDietByDietNo(dietReq)) {
			return ResponseEntity.ok("정상적으로 수정되었습니다.");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("요청을 처리하지 못했습니다.");
	}

	@PostMapping("/ai/vision-gpt")
	public ResponseEntity<?> analyzeByVisionGpt(@RequestParam("file") MultipartFile file) {
		try {
			List<Map<String, Object>> result = dietService.analyzeImageWithVisionAndGpt(file);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("분석 실패: " + e.getMessage());
			}
		}
	public ResponseEntity<?> analyzeByVisionGpt(@RequestParam("file") MultipartFile file,
												@CookieValue("access-token") String token) {
		try {
	    	
	        // 인증 처리
	        if (!util.validate(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
	        }
	        
	        List<Map<String, Object>> result = dietService.analyzeImageWithVisionAndGpt(file);
	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("분석 실패: " + e.getMessage());
	    }
	}

}
