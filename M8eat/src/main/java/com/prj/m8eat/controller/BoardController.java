package com.prj.m8eat.controller;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj.m8eat.jwt.JwtUtil;
import com.prj.m8eat.model.dto.Board;
import com.prj.m8eat.model.dto.BoardsComment;
import com.prj.m8eat.model.dto.Food;
import com.prj.m8eat.model.dto.User;
import com.prj.m8eat.model.service.BoardService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/boards")
public class BoardController {

	@Value("${file.upload.dir}")
	private String uploadDirPath;
	
	private final BoardService boardService;
	private final JwtUtil util;
	public BoardController(BoardService boardService, JwtUtil util) {
		this.boardService = boardService;
		this.util = util;
	}

	// 전체 게시글 조회
	@GetMapping
	public ResponseEntity<?> boardList() {
		List<Board> boardList = boardService.getBoardList();
		if (boardList == null || boardList.size() == 0) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Board>>(boardList, HttpStatus.OK);
	}

	// 게시글 상세 조회
	@GetMapping("/{boardNo}")
	public ResponseEntity<Board> boardDetail(@PathVariable int boardNo) {
		Board board = boardService.getBoardDetail(boardNo);
		if (board != null) {
			return ResponseEntity.ok(board);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

	}

	private final ObjectMapper objectMapper = new ObjectMapper();

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(List.class, "foods", new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				try {
					List<Food> foods = objectMapper.readValue(text, new TypeReference<List<Food>>() {
					});
					setValue(foods);
				} catch (IOException e) {
					throw new IllegalArgumentException("Invalid JSON format for foods", e);
				}
			}
		});
	}

	// 게시글 등록
	@PostMapping
	public ResponseEntity<String> boardWrite(@ModelAttribute Board board, 
											@CookieValue("access-token") String token) {
		System.out.println("boardwriteeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		System.out.println("boardddddddddddddddddddddddd " + board);

	    // ✅ 토큰 검증 + 정보 추출
	    if (util.validate(token)) {
	        Claims claims = util.getClaims(token);
	        Integer userNo = (Integer) claims.get("userNo"); // 또는 Long 형변환 조심
	        board.setUserNo(userNo); // ✅ 게시글 작성자 정보 설정
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
	    }
		
		
		MultipartFile file = board.getFile();
		
		if (file != null && !file.isEmpty()) {
			String originalFilename = file.getOriginalFilename();
//			String uploadDirPath = "/Users/jang-ayoung/Desktop/m8eat/data";

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

	// 게시글 삭제
	@DeleteMapping("/{boardNo}")
	public ResponseEntity<String> deleteBoard(@PathVariable int boardNo) {
		boolean isDeleted = boardService.removeBoard(boardNo);
		if (isDeleted) {
			return ResponseEntity.status(HttpStatus.OK).body("게시글이 성공적으로 삭제되었습니다!");
		} else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("없는 게시글 번호입니다");
	}

	// 게시글 수정
	@PutMapping(value = "/{boardNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> updateBoard(@PathVariable int boardNo, @ModelAttribute Board board) {
		board.setBoardNo(boardNo);
		MultipartFile file = board.getFile();

		// 새 파일이 업로드된 경우에만 저장
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
				board.setFilePath("/upload/" + originalFilename); // 새 파일로 대체
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 실패");
			}
		} else {
			// 새 파일이 없으면 기존 filePath를 null로 설정하거나 유지 안함
			board.setFilePath(null); // 또는 수정 안 할 수 있게 서비스 단에서 처리
		}

		int isSuccess = boardService.updateBoard(board);
		if (isSuccess >= 0) {
			return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다!");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 수정에 실패하였습니다");
		}
	}

	// 게시글 댓글 작성
	@PostMapping("/{boardNo}/comments")
	public ResponseEntity<String> commentWrite(@ModelAttribute BoardsComment comment) {
		int result = boardService.writeComment(comment);
		if (result == 1) {
			return ResponseEntity.ok("댓글 성공적으로 등록되었습니다!");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 작성에 실패했습니다");
		}
	}

	// 댓글 전체 조회
	@GetMapping("/{boardNo}/comments")
	public ResponseEntity<?> commentsList(@PathVariable int boardNo) {
		List<BoardsComment> commentList = boardService.getCommentList(boardNo);
		if (commentList == null || commentList.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("아직 등록된 댓글이 없습니다.");
		}
		return new ResponseEntity<List<BoardsComment>>(commentList, HttpStatus.OK);
	}

	// 댓글 수정
	@PutMapping("/{boardNo}/comments/{commentNo}")
	public ResponseEntity<?> updateComment(@PathVariable("boardNo") int boardNo,
			@PathVariable("commentNo") int commentNo, @RequestBody BoardsComment comment) {
		comment.setCommentNo(commentNo);
		int isSuccess = boardService.updateComment(comment, boardNo);
		if (isSuccess >= 0) {
			return ResponseEntity.status(HttpStatus.OK).body(comment);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 수정에 실패하였습니다");
		}
	}

	// 게시글 삭제
	@DeleteMapping("/{boardNo}/comments/{commentNo}")
	public ResponseEntity<String> deleteBoard(@PathVariable("boardNo") int boardNo,
			@PathVariable("commentNo") int commentNo) {
		boolean isDeleted = boardService.removeComment(boardNo, commentNo);
		if (isDeleted) {
			return ResponseEntity.status(HttpStatus.OK).body("댓글이 성공적으로 삭제되었습니다!");
		} else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 삭제에 실패하였습니다");
	}

	@PostMapping("/{boardNo}/likes")
	public ResponseEntity<?> addLikes(@PathVariable("boardNo") int boardNo, HttpSession session) {
		Object userObj = session.getAttribute("loginUser");

		if (userObj == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}

		int userNo = ((User) userObj).getUserNo();

		boolean isLiked = boardService.addLikes(boardNo, userNo); // 서비스 단에 userNo 전달

		if (isLiked) {
			return ResponseEntity.ok("좋아요가 등록되었습니다");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 좋아요를 눌렀습니다");
		}

	}

	// 게시글 좋아요 수 가져오기 boardNo 가 일치하는것들의 count 를 셈
	@GetMapping("/{boardNo}/likes")
	public int countLikes(@PathVariable int boardNo) {
		return boardService.countLikes(boardNo);
	}

	// 해당 사용자가 해당 게시글에 좋아요를 눌렀는지 확인하기
	@GetMapping("/{boardNo}/likes/check")
	public boolean countLikes(@PathVariable int boardNo, HttpSession session) {
		Object userObj = session.getAttribute("loginUser");
		int userNo = ((User) userObj).getUserNo();

		if (boardService.checkLiked(boardNo, userNo) == 1) {
			return true;
		} else
			return false;
	}

	@DeleteMapping("/{boardNo}/likes")
	public ResponseEntity<?> removeLikes(@PathVariable("boardNo") int boardNo, HttpSession session) {
		Object userObj = session.getAttribute("loginUser");

		if (userObj == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		int userNo = ((User) userObj).getUserNo();
		boolean isUnliked = boardService.removeLikes(boardNo, userNo); // 서비스 단에 userNo 전달

		if (isUnliked) {
			return ResponseEntity.ok("좋아요가 제거되었습니다.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요 취소에 실패했습니다");
		}

	}

}
