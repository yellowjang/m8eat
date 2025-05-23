<template>
  <section class="diet-detail">
    <h2>식단 상세 보기</h2>
    <div v-if="diet">
      <p>
        <strong>식단 날짜:</strong>
        {{ formatDate(diet.regDate) }}
      </p>
      <p>
        <strong>등록 시간:</strong>
        {{ formatTime(diet.regDate) }}
      </p>
      <div class="image-box" v-if="diet.filePath">
        <img :src="`http://localhost:8080${diet.filePath}`" alt="식단 이미지" class="diet-image" />
      </div>

      <h3>음식 목록</h3>
      <ul class="food-list">
        <li v-for="(food, idx) in diet.foods" :key="idx">
          <p>
            <strong>{{ food.foodName }}</strong>
          </p>
          <p>섭취량: {{ food.amount }}g</p>
          <p>열량: {{ food.calorie }} kcal</p>
          <p>탄수화물: {{ food.carbohydrate }}g | 단백질: {{ food.protein }}g | 지방: {{ food.fat }}g</p>
          <p>당: {{ food.sugar }}g | 콜레스테롤: {{ food.cholesterol }}mg</p>
        </li>
      </ul>
    </div>
    <div v-else>
      <p>식단 정보를 불러오는 중입니다...</p>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { useDietStore } from "@/stores/diet";

const route = useRoute();
const dietStore = useDietStore();
const diet = ref(null);

onMounted(async () => {
  const dietNo = route.params.dietNo;
  await dietStore.getDietDetail(dietNo);
  diet.value = dietStore.diet;
});

const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString("ko-KR");
};

const formatTime = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleTimeString("ko-KR");
};
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
