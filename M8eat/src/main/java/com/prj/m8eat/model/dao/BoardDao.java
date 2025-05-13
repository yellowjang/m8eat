package com.prj.m8eat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.prj.m8eat.model.dto.Board;

@Mapper
public interface BoardDao {
	public List<Board> selectBoardList();

	public Board selectBoard(int boardNo);

	public boolean deleteBoard(int boardNo);
}
