package com.prj.m8eat.model.service;

import java.util.List;

import com.prj.m8eat.model.dto.Board;
import com.prj.m8eat.model.dto.BoardsComment;

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

	public int writeComment(BoardsComment comment);

	public List<BoardsComment> getCommentList(int boardNo);

	public int updateComment(BoardsComment comment, int boardNo);

	public boolean removeComment(int boardNo, int commentNo);

	public boolean addLikes(int boardNo, int userNo);

	public int countLikes(int boardNo);

	public boolean removeLikes(int boardNo, int userNo);

	public int checkLiked(int boardNo, int userNo);

	
}
