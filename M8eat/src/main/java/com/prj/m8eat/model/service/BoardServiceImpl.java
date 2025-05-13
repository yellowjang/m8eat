package com.prj.m8eat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.BoardDao;
import com.prj.m8eat.model.dto.Board;

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
	public Board getBoard(int boardNo) {
		return boardDao.selectBoard(boardNo);
	}



	@Override
	public boolean removeBoard(int boardNo) {
		// TODO Auto-generated method stub
		return boardDao.deleteBoard(boardNo);
	}

}
