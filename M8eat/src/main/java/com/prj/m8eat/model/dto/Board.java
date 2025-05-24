package com.prj.m8eat.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class Board {
	private int boardNo;
	private int userNo;
	private String title;
	private String content;
	private int viewCnt;
	private String regDate;
	private MultipartFile file; //업로드용.. Request 시 사용
	private String filePath; //DB에서 불러올 실제 경로
	
	public int getBoardNo() {
		return boardNo;
	}
	public void setBoardNo(int boardNo) {
		this.boardNo = boardNo;
	}
	public int getUserNo() {
		return userNo;
	}
	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getViewCnt() {
		return viewCnt;
	}
	public void setViewCnt(int viewCnt) {
		this.viewCnt = viewCnt;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	@Override
	public String toString() {
		return "Board [boardNo=" + boardNo + ", userNo=" + userNo + ", title=" + title + ", content=" + content
				+ ", viewCnt=" + viewCnt + ", regDate=" + regDate + ", file=" + file + ", filePath=" + filePath + "]";
	}
	
	
}
