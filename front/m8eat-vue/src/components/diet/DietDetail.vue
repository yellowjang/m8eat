<template>
  <section class="diet-detail">
    <h2>식단 상세 보기</h2>
    <div v-if="dietDetail">
      <p>
        <strong>식단 날짜:</strong>
        {{ formatDate(dietDetail.mealDate) }}
      </p>
      <p>
        <strong>등록 시간:</strong>
        {{ formatTime(dietDetail.regDate) }}
      </p>
      <div class="image-box" v-if="dietDetail.filePath !== null">
        <img :src="getImgPath(dietDetail.filePath)" alt="식단 이미지" class="diet-image" />
      </div>

      <h3>음식 목록</h3>
      <ul class="food-list">
        <li v-for="(food, idx) in dietDetail.foods" :key="idx">
          <p>
            <strong>{{ food.foodName }}</strong>
          </p>
          <p>섭취량: {{ food.amount }}g</p>
          <p>열량: {{ food.calorie }} kcal</p>
          <p>탄수화물: {{ Number(food.carbohydrate).toFixed(3) }}g | 단백질: {{ Number(food.protein).toFixed(3) }}g | 지방: {{ Number(food.fat).toFixed(3) }}g</p>
          <p>당: {{ Number(food.sugar).toFixed(3) }}g | 콜레스테롤: {{ Number(food.cholesterol).toFixed(3) }}mg</p>
        </li>
      </ul>
      <NutrientBarChart :data="totalNutrients" />
    </div>
    <div v-else>
      <p>식단 정보를 불러오는 중입니다...</p>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import { useRoute } from "vue-router";
import { useDietStore } from "@/stores/diet";
import NutrientBarChart from "@/components/diet/NutrientBarChart.vue";

const route = useRoute();
const dietStore = useDietStore();
const diet = ref(null);
const dietDetail = ref(null);

onMounted(async () => {
  const dietNo = route.params.dietNo;
  await dietStore.getDietDetail(dietNo);
  console.log("✅ dietDetail:", dietStore.dietDetail); // ← 이게 null이면 백엔드 문제
  dietDetail.value = dietStore.dietDetail;
});
console.log("📦 route.params:", route.params);
const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString("ko-KR");
};

const getImgPath = (filePath) => {
  // const filePath = dietDetail.filePath;
  const img = `http://localhost:8080${filePath}`;
  return img;
};

const formatTime = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleTimeString("ko-KR");
};
const totalNutrients = computed(() => {
  return dietDetail.value?.foods?.reduce(
    (acc, food) => {
      acc.carbohydrate += food.carbohydrate || 0;
      acc.protein += food.protein || 0;
      acc.fat += food.fat || 0;
      acc.sugar += food.sugar || 0;
      acc.cholesterol += food.cholesterol || 0;
      return acc;
    },
    { carbohydrate: 0, protein: 0, fat: 0, sugar: 0, cholesterol: 0 }
  );
});
</script>

<style scoped>
.diet-detail {
  padding: 1rem;
  background: #fff8f8;
  border-radius: 10px;
  max-width: 600px;
  margin: auto;
}
.image-box {
  margin: 1rem 0;
}
.diet-image {
  max-width: 100%;
  border-radius: 8px;
}
.food-list {
  list-style: none;
  padding: 0;
}
.food-list li {
  border-top: 1px solid #ddd;
  padding: 0.5rem 0;
}
</style>
