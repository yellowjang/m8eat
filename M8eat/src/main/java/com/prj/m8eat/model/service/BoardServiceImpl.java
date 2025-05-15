package com.prj.m8eat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.BoardDao;
import com.prj.m8eat.model.dto.Board;
import com.prj.m8eat.model.dto.BoardsComment;

@Service
public class BoardServiceImpl implements BoardService{

	private final BoardDao boardDao;
	public BoardServiceImpl(BoardDao boardDao) {
		this.boardDao = boardDao;
	}



	@Override
	public List<Board> getBoardList() {
		return boardDao.selectBoardList();
	}


	@Override
	public Board getBoardDetail(int boardNo) {
		boardDao.updateViewCnt(boardNo);
		return boardDao.selectBoard(boardNo);
	}



	@Override
	public boolean removeBoard(int boardNo) {
		boardDao.deleteBoard(boardNo);
		return true;
	}



	@Override
	public int writeBoard(Board board) {
		return boardDao.insertBoard(board);
		
	}



	@Override
	public int updateBoard(Board board) {
		return boardDao.updateBoard(board);
		
	}



	@Override
	public int writeComment(BoardsComment comment) {
		return boardDao.insertBoardComment(comment);
	}



	@Override
	public List<BoardsComment> getCommentList(int boardNo) {
		return boardDao.selectCommentList(boardNo);
	}



	@Override
	public int updateComment(BoardsComment comment, int boardNo) {
		return boardDao.updateComment(comment,boardNo);
	}

}
