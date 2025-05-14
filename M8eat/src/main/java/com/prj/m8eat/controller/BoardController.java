package com.prj.m8eat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.model.dto.Board;
import com.prj.m8eat.model.service.BoardService;

@RestController
@RequestMapping("/boards")
public class BoardController {
	private final BoardService boardService;

	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}
	
	@GetMapping
	public ResponseEntity<?> boardList(){
		List<Board> boardList = boardService.getBoardList();
		if(boardList ==null || boardList.size() ==0) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Board>>(boardList, HttpStatus.OK);
	}
	
	@GetMapping("/{boardNo}")
	public ResponseEntity<Board> boardDetail(@PathVariable("boardNo")int boardNo){
		Board board = boardService.getBoard(boardNo);
		if(board!=null) {
			return ResponseEntity.ok(board);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
	}
	
	@DeleteMapping("/{boardNo}")
	public ResponseEntity<Integer> deleteBoard(@PathVariable("boardNo")int boardNo){
		boolean isDeleted = boardService.removeBoard(boardNo);
		if(isDeleted)
			return ResponseEntity.status(HttpStatus.OK).body("게시글이 성공적으로 삭제되었습니다!");
		boardService.removeBoard(boardNo);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("없는 게시글 번호입니다");
	}
	
	
	
}

