import { defineStore } from "pinia";
import axios from "axios";
import { ref } from "vue";
import api from "@/api";

const FOOD_API_URL = "http://localhost:8080/foods";

export const useFoodStore = defineStore("food", () => {
  const foods = ref([]);

  const fetchFoods = async () => {
    try {
      const response = await api.get(FOOD_API_URL);
      foods.value = response.data;
    } catch (error) {
      console.error("음식 데이터 불러오기 실패:", error);
    }
  };

  return {
    foods,
    fetchFoods,
  };
});
