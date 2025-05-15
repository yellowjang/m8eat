package com.prj.m8eat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.prj.m8eat.model.dto.Board;
import com.prj.m8eat.model.dto.BoardsComment;

@Mapper
public interface BoardDao {
	public List<Board> selectBoardList();

	public Board selectBoard(int boardNo);

	public boolean deleteBoard(int boardNo);

	public int insertBoard(Board board);

	public int updateBoard(Board board);

	public void updateViewCnt(int boardNo);

	public int insertBoardComment(BoardsComment comment);

	public List<BoardsComment> selectCommentList(int boardNo);

	public int updateComment(@Param("comment") BoardsComment comment, @Param("boardNo") int boardNo);
}
