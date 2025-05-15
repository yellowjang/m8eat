package com.prj.m8eat.model.service;

import java.util.List;

import com.prj.m8eat.model.dto.Board;

public interface BoardService {
	//게시글 전체 조회
	public List<Board> getBoardList();
	
	//게시글 조회 
	public Board getBoardDetail(int boardNo);

	//게시글 삭제
	public boolean removeBoard(int boardNo);

	//게시글 등록 
	public int writeBoard(Board board);

	//게시글 수정 
	public int updateBoard(Board board);
	
}
