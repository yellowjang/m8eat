package com.prj.m8eat.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	public ResponseEntity<?> boardList() {
		List<Board> boardList = boardService.getBoardList();
		if (boardList == null || boardList.size() == 0) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Board>>(boardList, HttpStatus.OK);
	}

	@GetMapping("/{boardNo}")
	public ResponseEntity<Board> boardDetail(@PathVariable int boardNo) {
		Board board = boardService.getBoard(boardNo);
		if (board != null) {
			return ResponseEntity.ok(board);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> boardWrite(@ModelAttribute Board board) {
		MultipartFile file = board.getFile();

		if (file != null && !file.isEmpty()) {
			String originalFilename = file.getOriginalFilename();
			String uploadDirPath = "/Users/jang-ayoung/Desktop/m8eat/data";

			File uploadDir = new File(uploadDirPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}

			try {
				File saveFile = new File(uploadDir, originalFilename);
				file.transferTo(saveFile);
				board.setFilePath("/upload/" + originalFilename); // 웹에서 접근 가능한 경로로 구성 (정적 리소스 매핑 필요)
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
			}
		}

		int result = boardService.writeBoard(board);
		if (result == 1) {
			return ResponseEntity.ok("게시글이 성공적으로 등록되었습니다!");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 등록에 실패했습니다");
		}
	}

	@DeleteMapping("/{boardNo}")
	public ResponseEntity<String> deleteBoard(@PathVariable int boardNo) {
		boolean isDeleted = boardService.removeBoard(boardNo);
		if (isDeleted) {
			return ResponseEntity.status(HttpStatus.OK).body("게시글이 성공적으로 삭제되었습니다!");
		} else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("없는 게시글 번호입니다");
	}

}
