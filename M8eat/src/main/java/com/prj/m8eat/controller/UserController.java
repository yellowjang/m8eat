package com.prj.m8eat.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prj.m8eat.model.dto.LoginResponse;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.dto.UserHealthInfo;
import com.prj.m8eat.model.service.UserService;
import com.prj.m8eat.security.CustomUserDetails;
import com.prj.m8eat.security.JwtTokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${file.upload.dir}")
    private String uploadDirPath;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    public ResponseEntity<?> signup(@RequestPart("user") User user,
                                    @RequestPart("healthInfo") UserHealthInfo healthInfo,
                                    @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        if (profileImage != null && !profileImage.isEmpty()) {
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            try {
                File saveFile = new File(uploadDir, profileImage.getOriginalFilename());
                profileImage.transferTo(saveFile);
                user.setProfileImagePath("/upload/" + profileImage.getOriginalFilename());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
            }
        }

        int result = userService.signup(user, healthInfo);
        return result == 1 ? ResponseEntity.status(HttpStatus.CREATED).build()
                           : ResponseEntity.badRequest().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        LoginResponse result = userService.login(user);
        if (result.isLogin()) {
            String token = jwtTokenProvider.createToken(result.getUser());
            ResponseCookie cookie = ResponseCookie.from("access-token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60)
                .build();
            response.setHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            Map<String, Object> res = new HashMap<>();
            res.put("message", result.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<User> checkLogin(@AuthenticationPrincipal CustomUserDetails userDetails) {
    	 if (userDetails == null) {
    	        System.out.println("🚨 인증 실패: userDetails가 null임");
    	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	    }
    	return ResponseEntity.ok(userDetails.getUser());
        
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from("access-token", "")
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .maxAge(0)
            .build();
        response.setHeader("Set-Cookie", expiredCookie.toString());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quit/{userNo}")
    public ResponseEntity<String> quit(@PathVariable("userNo") int userNo, HttpServletResponse response) {
        int result = userService.quit(userNo);
        if (result == 1) {
            ResponseCookie expiredCookie = ResponseCookie.from("access-token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
            response.setHeader("Set-Cookie", expiredCookie.toString());
            return ResponseEntity.ok("정상적으로 탈퇴되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("처리 실패");
        }
    }

    @GetMapping("/user/mypage")
    public ResponseEntity<User> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User myInfo = userService.getMyInfo(userDetails.getUser().getId());
        return myInfo != null ? ResponseEntity.ok(myInfo) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/mypage/healthInfo")
    public ResponseEntity<?> getMyHealthInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userNo = userDetails.getUser().getUserNo();

        UserHealthInfo healthInfo = userService.getMyHealthInfo(userNo);
        return healthInfo != null ? ResponseEntity.ok(healthInfo) : ResponseEntity.notFound().build();
    }


    @PutMapping("/user/mypage/health-info")
    public ResponseEntity<String> updateHealthInfo(@RequestBody UserHealthInfo healthInfo,
                                                   Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        healthInfo.setUserNo(userDetails.getUser().getUserNo());

        return userService.updateHealthInfo(healthInfo) == 1
            ? ResponseEntity.ok("수정되었습니다.")
            : ResponseEntity.badRequest().body("수정 실패");
    }

    @PutMapping(value = "/user/mypage/{userNo}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateMyInfo(@PathVariable("userNo") int userNo,
                                          @ModelAttribute User user,
                                          @RequestPart(value = "profileImage", required = false) MultipartFile file,
                                          HttpServletResponse response) {
        user.setUserNo(userNo);

        if (file != null && !file.isEmpty()) {
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            try {
                File saveFile = new File(uploadDir, file.getOriginalFilename());
                file.transferTo(saveFile);
                user.setProfileImagePath("/upload/" + file.getOriginalFilename());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 이미지 저장 실패");
            }
        }

        int result = userService.updateMyInfo(user);
        if (result > 0) {
            String newToken = jwtTokenProvider.createToken(user);
            Cookie cookie = new Cookie("access-token", newToken);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);
            return ResponseEntity.ok("회원 정보 수정 성공");
        }
        return ResponseEntity.badRequest().body("수정 실패");
    }

    @GetMapping("/user/mypage/coachId")
    public ResponseEntity<?> getCoachId(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userNo = userDetails.getUser().getUserNo();
        String coachId = userService.getCoachId(userNo);
        return coachId != null ? ResponseEntity.ok(coachId) : ResponseEntity.notFound().build();
    }
}
