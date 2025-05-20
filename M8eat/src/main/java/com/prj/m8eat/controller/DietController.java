package com.prj.m8eat.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

import com.prj.m8eat.model.dto.Diet;
import com.prj.m8eat.model.dto.DietRequest;
import com.prj.m8eat.model.dto.DietResponse;
import com.prj.m8eat.model.dto.DietsFood;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.service.DietService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/diets")
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
	public ResponseEntity<?> getDietsByDate(@RequestParam("start") String startDate, @RequestParam("end") String endDate) { 
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
	public ResponseEntity<String> writeDiets (@ModelAttribute DietRequest dietReq, HttpSession session) {
		
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
		};
		
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
	
}
