import { defineStore } from "pinia";
import { ref } from "vue";
import api from "@/api";

const REST_API_URL = `http://localhost:8080/coach`;

export const useCoachStore = defineStore("coach", () => {
  const members = ref([]);
  const diets = ref({});

  const tokenHeader = () => ({
    headers: {
      "access-token": sessionStorage.getItem("access-token"),
    },
  });

  /** 담당 회원 목록 조회 */
  const fetchMembers = async () => {
    try {
      const res = await api.get(`${REST_API_URL}/members`, tokenHeader());
      members.value = res.data;
      return res.data;
    } catch (err) {
      console.error("회원 목록 불러오기 실패:", err);
      throw err;
    }
  };

  /** 회원별 식단 조회 */
  const fetchMemberDiets = async (userNo) => {
    try {
      const res = await api.get(`${REST_API_URL}/member/${userNo}/diets`, tokenHeader());
      diets.value[userNo] = res.data;
      return res.data;
    } catch (err) {
      console.error(`회원 ${userNo} 식단 불러오기 실패:`, err);
      throw err;
    }
  };

  return {
    members,
    diets,
    fetchMembers,
    fetchMemberDiets,
  };
});
