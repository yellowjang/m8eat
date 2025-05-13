package com.prj.m8eat.model.service;

import java.util.List;

import com.prj.m8eat.model.dto.Board;

public interface BoardService {
	//게시글 전체 조회
	public List<Board> getBoardList();
	
	//게시글 조회 
	public Board getBoard(int boardId);
	
}
