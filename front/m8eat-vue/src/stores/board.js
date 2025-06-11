import { ref } from "vue";
import { defineStore } from "pinia";
import router from "@/router";
import api from "@/api";

const REST_API_URL = `http://localhost:8080/boards`;

export const useBoardStore = defineStore("board", () => {
  // 상태
  const commentsList = ref([]);

  // 게시판 목록
  const getBoardList = async () => {
    try {
      const response = await api.get(`${REST_API_URL}`);
      console.log("getBoardList Store", response.data);
      return response.data;
    } catch (error) {
      console.error("게시판 목록 불러오기 실패", error);
      throw error;
    }
  };

  // 게시글 상세
  const getBoardDetail = async (boardNo) => {
    try {
      const response = await api.get(`${REST_API_URL}/${boardNo}`);
      return response.data;
    } catch (error) {
      console.error("게시판 상세 정보 불러오기 실패", error);
      throw error;
    }
  };

  // 게시글 삭제
  const removeBoard = async (boardNo) => {
    try {
      await api.delete(`${REST_API_URL}/${boardNo}`);
    } catch (error) {
      console.error("게시글 삭제 실패", error);
      throw error;
    }
  };

  // 게시글 등록
  const addBoard = async (formData) => {
    try {
      await api.post(`${REST_API_URL}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
    } catch (error) {
      console.error("게시글 등록 실패: ", error);
      throw error;
    }
  };

  // 게시글 수정
  const updateBoard = async (formData, boardNo) => {
    try {
      await api.put(`${REST_API_URL}/${boardNo}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
    } catch (error) {
      console.error("게시글 수정 실패: ", error);
      throw error;
    }
  };

  // ✅ 댓글 목록 조회
  const getComments = async (boardNo) => {
    try {
      const response = await api.get(`${REST_API_URL}/${boardNo}/comments`);
      commentsList.value = response.data;
    } catch (error) {
      console.error("댓글 조회 실패", error);
      throw error;
    }
  };

  // ✅ 댓글 작성
  const writeComment = async (boardNo, commentData) => {
    try {
      await api.post(`${REST_API_URL}/${boardNo}/comments`, commentData);
      await getComments(boardNo); // 등록 후 최신화
    } catch (error) {
      console.error("댓글 작성 실패", error);
      throw error;
    }
  };

  // ✅ 댓글 수정
  const updateComment = async (boardNo, commentNo, content) => {
    try {
      await api.put(`${REST_API_URL}/${boardNo}/comments/${commentNo}`, {
        content,
      });
      await getComments(boardNo); // 수정 후 최신화
    } catch (error) {
      console.error("댓글 수정 실패", error);
      throw error;
    }
  };

  // ✅ 댓글 삭제
  const deleteComment = async (boardNo, commentNo) => {
    try {
      await api.delete(`${REST_API_URL}/${boardNo}/comments/${commentNo}`);
      await getComments(boardNo); // 삭제 후 최신화
    } catch (error) {
      console.error("댓글 삭제 실패", error);
      throw error;
    }
  };

  return {
    getBoardList,
    getBoardDetail,
    removeBoard,
    addBoard,
    updateBoard,
    commentsList,
    getComments,
    writeComment,
    updateComment,
    deleteComment,
  };
});
