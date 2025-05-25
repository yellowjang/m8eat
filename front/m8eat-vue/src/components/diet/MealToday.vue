<template>
  <section class="meal-today">
    <h2 class="title">{{ formattedDate }} 식단</h2>
    <div class="add-meal-box">
      <button class="add-meal" @click="$emit('add-meal')">
        + 식단 등록하기
      </button>
    </div>
    <div class="meal-boxes">
      <div
        class="meal-box"
        v-for="type in ['아침', '점심', '저녁']"
        :key="type"
      >
        <p class="meal-title">{{ type }}</p>

        <div v-if="mealsByType(type).length > 0">
          <button
            class="edit-meal"
            @click="$emit('edit-meal', mealsByType(type)[0])"
          >
            수정
          </button>
          <div
            class="meal-item"
            v-for="meal in mealsByType(type)"
            :key="meal.dietNo"
          >
            <router-link class="view-detail" :to="`/diet/${meal.dietNo}`">
              상세보기
            </router-link>
            <button class="delete-meal" @click="deleteMeal(meal.dietNo)">
              삭제
            </button>
            <ul class="food-list">
              <li
                v-for="(food, idx) in meal.foods"
                :key="idx"
                class="food-name"
              >
                {{ food.foodName }}
              </li>
            </ul>
            <p class="calorie">{{ totalCalories(meal.foods) }} kcal</p>
          </div>
        </div>

        <div v-else class="empty-meal">등록된 식단이 없습니다.</div>
      </div>
    </div>
    <p class="summary">총 섭취 칼로리: {{ overallCalories }} kcal</p>
    <div class="divide-line"></div>
    <div class="meal-analysis">
      <p class="title nutri">영양 성분 분석</p>
      <NutrientGraph :data="totalNutrients" :max="recommendedIntake" />
    </div>
  </section>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useDietStore } from "@/stores/diet";
import NutrientGraph from "@/components/diet/NutrientGraph.vue";
import dayjs from "dayjs";

const props = defineProps({
  selectedDate: String,
});

const dietStore = useDietStore();
const meals = ref([]);

const fetchDiets = async (date) => {
  try {
    const start = `${date} 00:00:00`;
    const end = `${date} 23:59:59`;
    await dietStore.getDietByDate(start, end);
    console.log("📦 dietByDateList:", dietStore.dietByDateList); // 여기도 확인
    meals.value = [...dietStore.dietByDateList];
  } catch (e) {
    console.error("식단 불러오기 실패", e);
    meals.value = [];
  }
};

watch(
  () => props.selectedDate,
  (newDate) => {
    console.log("📦 selectedDate 변경 감지:", newDate);
    if (newDate) fetchDiets(newDate);
  },
  { immediate: true }
);

const mealsByType = (type) => {
  return meals.value.filter((meal) => meal.mealType === type);
};

const totalCalories = (foods) =>
  foods.reduce((sum, food) => sum + (food.calorie || 0), 0);
const overallCalories = computed(() =>
  meals.value.reduce((total, meal) => total + totalCalories(meal.foods), 0)
);
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
const deleteMeal = async (dietNo) => {
  if (confirm("정말로 이 식단을 삭제하시겠습니까?")) {
    try {
      await dietStore.deleteDiet(dietNo);
      alert("식단이 삭제되었습니다.");
      await fetchDiets(props.selectedDate);
    } catch (e) {
      console.error("삭제 중 오류 발생:", e);
      alert("삭제 중 오류가 발생했습니다.");
    }
  }
};
const recommendedIntake = {
  carbohydrate: 324,
  protein: 55,
  fat: 54,
  sugar: 50,
};

const formattedDate = computed(() =>
  dayjs(props.selectedDate).format("YYYY년 MM월 DD일")
);
</script>

<style lang="scss" scoped>
.view-detail {
  color: #de9c9c;
  border: none;
  font-size: 12px;
  font-weight: bold;
  text-decoration: none;
}

.view-detail:hover {
  color: #c94e4e;
}

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
  background-color: transparent;
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

.delete-meal {
  background: none;
  border: none;
  color: #c94e4e;
  font-size: 12px;
  margin-left: 5px;
  cursor: pointer;
  font-weight: bold;
}

.delete-meal:hover {
  color: #de9c9c;
  border: none;
  font-size: 12px;
  font-weight: bold;
  text-decoration: none;
}
</style>
