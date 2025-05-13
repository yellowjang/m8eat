package com.prj.m8eat.model.dto;

import org.springframework.web.multipart.MultipartFile;

public class Boards {
	private int boardNo;
	private int userNo;
	private String title;
	private String content;
	private int viewCnt;
	private String regDate;
	private MultipartFile file;
}
