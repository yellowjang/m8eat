<template>
  <div class="meal-calendar-layout">
    <div class="calendar-pane">
      <Calendar @select-date="handleDateChange" />
    </div>
    <div class="meal-pane">
      <Modal v-if="selectedMeal === 'create'" @close="resetSelection">
        <MealForm :selectedDate="selectedDate" @close="resetSelection" @add-meal="refreshMeals" />
      </Modal>
      <Modal v-if="selectedMeal && selectedMeal !== 'create'" @close="resetSelection">
        <MealEditForm :edit="selectedMeal" @close="resetSelection" @update-meal="refreshMeals" />
      </Modal>
      <MealToday v-if="!selectedMeal" :selectedDate="selectedDate" @add-meal="selectedMeal = 'create'" @edit-meal="handleEditMeal" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import Calendar from "@/components/diet/Calendar.vue";
import MealToday from "@/components/diet/MealToday.vue";
import MealForm from "@/components/diet/MealForm.vue";
import MealEditForm from "@/components/diet/MealEditForm.vue";
import Modal from "@/components/common2/Modal.vue";
import { useDietStore } from "@/stores/diet";
import dayjs from "dayjs";

const selectedDate = ref(dayjs().format("YYYY-MM-DD")); // 오늘 날짜를 기본값으로
const selectedMeal = ref(null);
// const selectedDateRef = ref(new Date().toISOString().slice(0, 10));
// const selectedDate = computed(() => selectedDateRef.value); // 반응형으로 wrap
const dietStore = useDietStore();

const resetSelection = () => {
  selectedMeal.value = null;
};

const refreshMeals = async () => {
  selectedMeal.value = null;
  await dietStore.getDietByDate(selectedDate.value, selectedDate.value);
};

const handleEditMeal = async (meal) => {
  await dietStore.getDietDetail(meal.dietNo);
  selectedMeal.value = JSON.parse(JSON.stringify(dietStore.dietDetail));
};

const handleDateChange = async (date) => {
  console.log("📌 전달받은 날짜:", date); // 여기도 확인
  selectedDate.value = date;
  await dietStore.getDietByDate(date, date);
};
</script>

<style scoped>
.meal-calendar-layout {
  display: flex;
  gap: 5rem;
  padding: 2rem;
}
.calendar-pane {
  flex: 1;
}
.meal-pane {
  flex: 2;
}
</style>
