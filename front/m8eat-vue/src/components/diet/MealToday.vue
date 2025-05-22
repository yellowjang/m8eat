<template>
  <section class="meal-today">
    <h2 class="title">오늘의 식단</h2>
    <!-- showForm이 false일 때만 기존 박스 보여줌 -->
    <template v-if="!showForm">
      <div class="meal-boxes">
        <div v-for="meal in meals" :key="meal.type" class="meal-box">
          <p class="meal-title">{{ meal.label }}</p>
          <ul class="food-list">
            <li v-for="(food, idx) in meal.foods" :key="idx" class="food-name">
              {{ food.name }}
            </li>
          </ul>
          <div>
            <p class="calorie">{{ totalCalories(meal.foods) }} kcal</p>
          </div>
        </div>
      </div>
      <p class="summary">총 섭취 칼로리: {{ overallCalories }} kcal</p>
      <div>
        <p>식단 분석</p>
        <div>
          <!-- 영양성분 분석 그래프 들어감. -->
          <!-- 막대 그래프에는 하루 권장 섭취량이 전체 섭취량이 되고, 현재 섭취한 영양성분은
              비율로 현재까지의 섭취율을 표기한다. 이건 탄수화물, 단백질, 지방, 당류로 구분하여
              4개의 원형 그래프로 나타내려고 함.-->
        </div>
      </div>
      <div class="add-meal-box">
        <button class="add-meal" @click="showForm = true">+ 식단 등록하기</button>
      </div>
    </template>

    <!-- showForm이 true일 때는 MealForm만 표시 -->
    <MealForm v-else @close="showForm = false" @add-meal="addMeal" />
  </section>
</template>

<script setup>
import { ref, computed } from "vue";
import MealForm from "./MealForm.vue";

const meals = ref([
  {
    type: "breakfast",
    label: "아침",
    foods: [
      { name: "계란후라이", calorie: 80 },
      { name: "토스트", calorie: 150 },
    ],
  },
  {
    type: "lunch",
    label: "점심",
    foods: [
      { name: "비빔밥", calorie: 500 },
      { name: "김치", calorie: 50 },
    ],
  },
  {
    type: "dinner",
    label: "저녁",
    foods: [
      { name: "닭가슴살", calorie: 180 },
      { name: "고구마", calorie: 100 },
    ],
  },
]);

const showForm = ref(false);

const totalCalories = (foods) => foods.reduce((sum, food) => sum + food.calorie, 0);

const overallCalories = computed(() => meals.value.reduce((total, meal) => total + totalCalories(meal.foods), 0));

const addMeal = (newMeal) => {
  const target = meals.value.find((m) => m.type === newMeal.type);
  if (target) {
    target.foods.push(...newMeal.foods);
  }
  showForm.value = false;
};
</script>
<style lang="scss" scoped>
.title {
  font-size: 20px;
  font-weight: 700;
}

.meal-record {
  display: flex;
  gap: 2rem;
  padding: 2rem;
  background-color: #fcecec;

  .left-panel,
  .right-panel {
    flex: 1;
    background: white;
    padding: 2rem;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  }

  h2 {
    font-size: 1.5rem;
    margin-bottom: 1rem;
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
  .meal-analysis {
    margin-top: 3rem;

    .circle {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      border: 10px solid #de9c9c;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      margin: auto;

      .left-kcal {
        font-size: 1.5rem;
        font-weight: bold;
      }
    }

    .nutrient-bars {
      margin-top: 2rem;

      .bar {
        height: 20px;
        background: #ffe6e6;
        margin-bottom: 0.5rem;
        border-radius: 4px;
        padding-left: 0.5rem;
      }
    }

    .calorie-summary {
      margin-top: 1.5rem;

      strong {
        font-weight: bold;
      }
    }
  }

  .calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .greeting {
      font-size: 0.9rem;
      color: #999;
    }
  }

  .calendar {
    margin-top: 1.5rem;
    background: #fff8f8;
    border-radius: 8px;
    height: 500px;
    padding: 1rem;
    text-align: center;
  }

  .calendar-summary {
    margin-top: 2rem;

    .min {
      color: green;
    }

    .max {
      color: red;
    }
  }
}
</style>
