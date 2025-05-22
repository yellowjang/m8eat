import { defineStore } from "pinia";
import axios from "axios";

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
  const createDiet = async (form) => {
    try {
      const formData = new FormData();

      // 이미지 파일 추가
      if (form.imageFile) {
        formData.append("image", form.imageFile);
      }

      // JSON 데이터 추가 (필요 시 stringify 사용)
      formData.append("mealTime", form.mealTime);

      form.foods.forEach((food, index) => {
        formData.append(`foods[${index}].name`, food.name);
        formData.append(`foods[${index}].amount`, food.amount);
        formData.append(`foods[${index}].kcal`, food.kcal);
      });

      await axios.post(`${REST_API_URL}`, formData, {
        headers: {
          "access-token": sessionStorage.getItem("access-token"),
          "Content-Type": "multipart/form-data",
        },
      });
    } catch (err) {
      console.error("식단 등록 실패", err);
    }
  };

  /** 날짜별 식단 조회 */
  const getDietByDate = async (start, end) => {
    try {
      const res = await axios.get(`${REST_API_URL}/date?start=${start}&end=${end}`, tokenHeader());
      dietList.value = res.data;
    } catch (err) {
      console.error("날짜별 식단 조회 실패", err);
    }
  };

  /** 유저별 식단 조회 */
  /** 코치가 유저별로 식단을 조회해야 할 것임 */
  const getDietByUser = async (userNo) => {
    try {
      const res = await axios.get(`${REST_API_URL}/user/${userNo}`, tokenHeader());
      dietList.value = res.data;
    } catch (err) {
      console.error("유저별 식단 조회 실패", err);
    }
  };

  /** 식단 상세 조회 */
  const getDietDetail = async (dietNo) => {
    try {
      const res = await axios.get(`${REST_API_URL}/${dietNo}`, tokenHeader());
      diet.value = res.data;
    } catch (err) {
      console.error("식단 상세 조회 실패", err);
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
      // 삭제 후 리스트 갱신 등 추가 가능
    } catch (err) {
      console.error("식단 삭제 실패", err);
    }
  };

  return {
    dietList,
    diet,
    getDietList,
    getDietByDate,
    getDietByUser,
    getDietDetail,
    updateDiet,
    deleteDiet,
  };
});
