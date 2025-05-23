<template>
  <section class="meal-today">
    <h2 class="title">오늘의 식단</h2>
    <template v-if="!showForm">
      <div class="meal-boxes">
        <div v-for="meal in meals" :key="meal.type" class="meal-box">
          <p class="meal-title">{{ meal.label }}</p>
          <ul class="food-list">
            <li v-for="(food, idx) in meal.foods" :key="idx" class="food-name">
              {{ food.foodName }}
            </li>
          </ul>
          <div>
            <p class="calorie">{{ totalCalories(meal.foods) }} kcal</p>
          </div>
          <button class="edit-meal" @click="$emit('edit-meal', meal)">수정</button>
        </div>
      </div>
      <p class="summary">총 섭취 칼로리: {{ overallCalories }} kcal</p>
      <div class="divide-line"></div>
      <div class="meal-analysis">
        <p class="title nutri">영양 성분 분석</p>
        <NutrientGraph :data="totalNutrients" :max="recommendedIntake" />
      </div>
      <div class="add-meal-box">
        <button class="add-meal" @click="$emit('add-meal')">+ 식단 등록하기</button>
      </div>
    </template>

    <!-- <MealForm :edit="selectedMeal" @close="closeForm" @update-meal="updateMeal" @add-meal="addMeal" /> -->
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import MealForm from "./MealForm.vue";
import NutrientGraph from "@/components/diet/NutrientGraph.vue";
import { useDietStore } from "@/stores/diet";
import dayjs from "dayjs";

const dietStore = useDietStore();
const meals = ref([]);
const showForm = ref(false);
const today = dayjs().format("YYYY-MM-DD");

const fetchTodayDiets = async () => {
  try {
    await dietStore.getDietByDate(today, today);
    const rawData = dietStore.dietList;
    const grouped = ["아침", "점심", "저녁"].map((type) => ({
      type,
      label: type,
      foods: rawData.filter((d) => d.mealType === type).flatMap((d) => d.foods || []),
    }));
    meals.value = grouped;
  } catch (e) {
    console.error("식단 불러오기 실패", e);
  }
};

onMounted(fetchTodayDiets);

const totalCalories = (foods) => foods.reduce((sum, food) => sum + (food.calorie || 0), 0);
const overallCalories = computed(() => meals.value.reduce((total, meal) => total + totalCalories(meal.foods), 0));
const totalNutrients = computed(() => {
  return meals.value.reduce(
    (acc, meal) => {
      for (const food of meal.foods || []) {
        acc.carbohydrate += food.carbohydrate || 0;
        acc.protein += food.protein || 0;
        acc.fat += food.fat || 0;
        acc.sugar += food.sugar || 0;
      }
      return acc;
    },
    { carbohydrate: 0, protein: 0, fat: 0, sugar: 0 }
  );
});
const editMeal = (meal) => {
  // editingMeal.value = meal;
  showForm.value = true;
};
const recommendedIntake = {
  carbohydrate: 324,
  protein: 55,
  fat: 54,
  sugar: 50,
};

const addMeal = () => {
  fetchTodayDiets();
  showForm.value = false;
};
</script>

<style lang="scss" scoped>
.divide-line {
  border-bottom: solid 0.3px #f1caca;
  margin: 10px 0px;
}
.title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 15px;
}

.meal-boxes {
  display: flex;
  gap: 1rem;
  .meal-box {
    flex: 1;
    background: #fff5f5;
    padding: 1rem;
    border-radius: 8px;
    text-align: center;
    font-weight: bold;
  }
}
.meal-title {
  font-size: 16px;
  font-weight: 700;
}
.food-list {
  margin: 15px 10px;
}
.food-name {
  font-size: 12px;
  font-weight: 400;
  list-style: none;
}
.calorie {
  font-size: 12px;
  font-weight: 500;
  color: #d26767;
}
.summary {
  margin-top: 1rem;
  font-weight: 700;
  font-size: 16px;
  color: #4e4949;
  background-color: #ececec 70%;
  border-radius: 3px;
  width: 200px;
  text-align: center;
  margin-left: auto;
}
.add-meal {
  text-align: right;
  margin-left: auto;
  margin-top: 0.5rem;
  font-size: 14px;
  color: #de9c9c;
  font-weight: 600;
  border: none;
}
.add-meal-box {
  display: flex;
}

.nutrient-graph {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 1rem;
}
</style>
