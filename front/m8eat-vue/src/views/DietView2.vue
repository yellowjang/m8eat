<template>
  <p class="section-title">식단 기록</p>
  <div class="meal-record">
    <!-- 좌측 패널 -->
    <div class="left-panel">
      <MealForm v-if="isAddingMeal" />
      <MealToday v-else @add-meal="isAddingMeal = true" />
      <!-- 식단 분석은 그대로 유지할 수도 있음 -->
    </div>

    <!-- 우측 패널 -->
    <div class="right-panel">
      <section class="meal-calendar">
        <div class="calendar-header">
          <h2>식단 캘린더</h2>
        </div>

        <div class="calendar">
          <p>📅 2025년 5월 (달력 자리)</p>
        </div>

        <div class="calendar-summary">
          <h3>5월 식단 통계</h3>
          <p>
            평균 일일 칼로리:
            <strong>1,620 kcal</strong>
          </p>
          <p>
            최소 칼로리:
            <strong class="min">1,240 kcal</strong>
          </p>
          <p>
            최대 칼로리:
            <strong class="max">2,150 kcal</strong>
          </p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import MealForm from "@/components/diet/MealForm.vue";
import MealToday from "@/components/diet/MealToday.vue";
const isAddingMeal = ref(false);
</script>

<style scoped>
.section-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 20px;
}
.meal-record {
  display: flex;
  gap: 2rem;
  /* padding: 2rem; */
  background-color: #fcecec;
}
.left-panel,
.right-panel {
  flex: 1;
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
</style>
