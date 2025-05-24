import { ref } from "vue";
import { defineStore } from "pinia";
import router from "@/router";
import api from "@/api";
import BoardList from "@/components/board/BoardList.vue";

const REST_API_URL = `http://localhost:8080/boards`;

// base64url 디코딩 함수 (한글 깨짐 방지)
function base64UrlDecode(str) {
  // base64url -> base64로 변환
  str = str.replace(/-/g, "+").replace(/_/g, "/");
  // 패딩 추가
  while (str.length % 4) {
    str += "=";
  }
  // 디코딩 (한글 지원)
  try {
    return decodeURIComponent(Array.prototype.map.call(atob(str), (c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)).join(""));
  } catch (e) {
    return atob(str);
  }
}

export const useBoardStore = defineStore("board", () => {
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

  const getBoardDetail = async (boardNo) => {
    try {
      const response = await api.get(`${REST_API_URL}/${boardNo}`);
      console.log(response.data);
      return response.data;
    } catch (error) {
      console.error("게시판 상세 정보 불러오기 실패", error);
      throw error;
    }
  };

  const removeBoard = async (boardNo) => {
    try {
      const response = await api.delete(`${REST_API_URL}/${boardNo}`);
      console.log(response.data);
    } catch {
      console.error("게시글 삭제 실패", error);
      throw error;
    }
  };

  const addBoard = async (formData) => {
    const config = {
      headers: { "Content-Type": "multipart/form-data" },
    };

    try {
      await api.post("http://localhost:8080/boards", formData, config);

    } catch (error) {
      console.error("게시글 등록 실패: ", error);
      throw error;
    }
  };

  return { getBoardList, getBoardDetail, removeBoard, addBoard };
});
