import { defineStore } from "pinia";
import axios from "axios";
import { ref } from "vue";
const REST_API_URL = `http://localhost:8080/diets`;

export const useDietStore = defineStore("diets", () => {
  const dietList = ref([]);
  const diet = ref(null);

  const tokenHeader = () => ({
    headers: {
      "access-token": sessionStorage.getItem("access-token"),
    },
  });

  /** 식단 등록 (이미지 포함 멀티파트 전송) */
  const createDiet = async (formData) => {
    try {
      await axios.post(`${REST_API_URL}`, formData, tokenHeader());
    } catch (err) {
      console.error("식단 등록 실패", err);
      throw err;
    }
  };

  /** 날짜별 식단 조회 */
  const getDietByDate = async (start, end) => {
    try {
      const res = await axios.get(
        `${REST_API_URL}/date?start=${start}&end=${end}`,
        tokenHeader()
      );
      dietList.value = res.data;
    } catch (err) {
      console.error("날짜별 식단 조회 실패", err);
    }
  };

  /** 유저별 식단 조회 */
  const getDietByUser = async (userNo) => {
    try {
      const res = await axios.get(
        `${REST_API_URL}/user/${userNo}`,
        tokenHeader()
      );
      dietList.value = res.data;
    } catch (err) {
      console.error("유저별 식단 조회 실패", err);
    }
  };

  /** 식단 상세 조회 */
  const getDietDetail = async (dietNo) => {
    try {
      const res = await axios.get(`${REST_API_URL}/${dietNo}`, tokenHeader());
      if (res.data && res.data.length > 0) {
        diet.value = res.data[0];
      } else {
        diet.value = null;
      }
    } catch (err) {
      console.error("식단 상세 조회 실패", err);
      diet.value = null;
    }
  };
  /** 식단 수정 */
  const updateDiet = async (dietNo, payload) => {
    try {
      await axios.put(`${REST_API_URL}/${dietNo}`, payload, tokenHeader());
    } catch (err) {
      console.error("식단 수정 실패", err);
    }
  };

  /** 식단 삭제 */
  const deleteDiet = async (dietNo) => {
    try {
      await axios.delete(`${REST_API_URL}/${dietNo}`, tokenHeader());
    } catch (err) {
      console.error("식단 삭제 실패", err);
    }
  };

  return {
    dietList,
    diet,
    createDiet,
    getDietByDate,
    getDietByUser,
    getDietDetail,
    updateDiet,
    deleteDiet,
  };
});
