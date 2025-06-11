package com.prj.m8eat.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class User {
	private int userNo;
	private String name;
	private String id;
	private String password;
	private String role;
	private MultipartFile profileImage;
	private String profileImagePath;     // ✅ 프로필 이미지 경로
	private Integer coachNo;         // ✅ 담당 코치 번호 (nullable)
	private List<Integer> managedUserNos; // ✅ 담당 회원 리스트 (선택: 코치일 때만)

	
	
	public User() {
	}

	public int getUserNo() {
		return userNo;
	}
	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public MultipartFile getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(MultipartFile profileImage) {
		this.profileImage = profileImage;
	}
	public String getProfileImagePath() {
		return profileImagePath;
	}
	public void setProfileImagePath(String profileImagePath) {
		this.profileImagePath = profileImagePath;
	}
	public Integer getCoachNo() {
		return coachNo;
	}
	public void setCoachNo(Integer coachNo) {
		this.coachNo = coachNo;
	}
	public List<Integer> getManagedUserNos() {
		return managedUserNos;
	}
	public void setManagedUserNos(List<Integer> managedUserNos) {
		this.managedUserNos = managedUserNos;
	}

	@Override
	public String toString() {
		return "User [userNo=" + userNo + ", name=" + name + ", id=" + id + ", password=" + password + ", role=" + role
				+ ", profileImage=" + profileImage + ", profileImagePath=" + profileImagePath + ", coachNo=" + coachNo
				+ ", managedUserNos=" + managedUserNos + "]";
	}

	
}
