package com.prj.m8eat.model.dto;

import java.time.LocalDateTime;

public class ChatRoom {
	
	private Long id;
	private String user1;
    private String user2;
    private LocalDateTime createdAt;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUser1() {
		return user1;
	}
	public void setUser1(String user1) {
		this.user1 = user1;
	}
	public String getUser2() {
		return user2;
	}
	public void setUser2(String user2) {
		this.user2 = user2;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
    
	
	
//	private int roomNo;
//	private String roomType;
//	private String createdAt;
//	public int getRoomNo() {
//		return roomNo;
//	}
//	public void setRoomNo(int roomNo) {
//		this.roomNo = roomNo;
//	}
//	public String getRoomType() {
//		return roomType;
//	}
//	public void setRoomType(String roomType) {
//		this.roomType = roomType;
//	}
//	public String getCreatedAt() {
//		return createdAt;
//	}
//	public void setCreatedAt(String createdAt) {
//		this.createdAt = createdAt;
//	}
	
	
}
