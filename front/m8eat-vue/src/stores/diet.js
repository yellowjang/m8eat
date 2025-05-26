import { defineStore } from "pinia";
import axios from "axios";
import { ref } from "vue";
import api from "@/api";

const REST_API_URL = `http://localhost:8080/diets`;

export const useDietStore = defineStore("diets", () => {
  // ✅ 목적별 리스트 구분
  const dietByDateList = ref([]); // 날짜별 조회용
  const dietByUserList = ref([]); // 유저별 조회용
  const allDietList = ref([]); // 전체 조회용
  const dietDetail = ref(null); // 단일 상세 조회용

  const tokenHeader = () => ({
    headers: {
      "access-token": sessionStorage.getItem("access-token"),
    },
  });

  /** 식단 등록 (이미지 포함 멀티파트 전송) */
  const createDiet = async (formData) => {
    try {
      console.log("createDiet", formData)
      await api.post(`${REST_API_URL}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      });
    } catch (err) {
      console.error("식단 등록 실패", err);
      throw err;
    }
  };

  /** 전체 식단 조회 */
  const getAllDiets = async () => {
    try {
      const res = await api.get(`${REST_API_URL}`, tokenHeader());
      allDietList.value = res.data;
    } catch (err) {
      console.error("전체 식단 조회 실패", err);
    }
  };

  /** 날짜별 식단 조회 */
  const getDietByDate = async (start, end) => {
    try {
      const res = await api.get(
        `${REST_API_URL}/date?start=${start}&end=${end}`,
        tokenHeader()
      );
      dietByDateList.value = res.data;
    } catch (err) {
      console.error("날짜별 식단 조회 실패", err);
    }
  };

  /** 유저별 식단 조회 */
  const getDietByUser = async (userNo) => {
    try {
      const res = await api.get(
        `${REST_API_URL}/user/${userNo}`,
        tokenHeader()
      );
      dietByUserList.value = res.data;
    } catch (err) {
      console.error("유저별 식단 조회 실패", err);
    }
  };

  /** 식단 상세 조회 */
  const getDietDetail = async (dietNo) => {
    try {
      const res = await api.get(`${REST_API_URL}/${dietNo}`, tokenHeader());
      if (res.data) {
        dietDetail.value = Array.isArray(res.data) ? res.data[0] : res.data;
      } else {
        dietDetail.value = null;
      }
    } catch (err) {
      console.error("식단 상세 조회 실패", err);
      dietDetail.value = null;
    }
  };

  /** 식단 수정 */
  const updateDiet = async (dietNo, formData) => {
    try {
      await api.put(`${REST_API_URL}/${dietNo}`, formData, tokenHeader());
    } catch (err) {
      console.error("식단 수정 실패", err);
    }
  };

  /** 식단 삭제 */
  const deleteDiet = async (dietNo) => {
    try {
      await api.delete(`${REST_API_URL}/${dietNo}`, tokenHeader());
    } catch (err) {
      console.error("식단 삭제 실패", err);
    }
  };

  return {
    // 🔽 export 되는 state
    allDietList,
    dietByDateList,
    dietByUserList,
    dietDetail,

    // 🔽 export 되는 actions
    createDiet,
    getAllDiets,
    getDietByDate,
    getDietByUser,
    getDietDetail,
    updateDiet,
    deleteDiet,
  };
});
