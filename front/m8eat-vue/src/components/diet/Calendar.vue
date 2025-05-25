<script setup>
import { ref, onMounted, nextTick } from "vue";
import { VueCal } from "vue-cal";
import dayjs from "dayjs";
import { useDietStore } from "@/stores/diet";

const showWeekends = ref(true);
const toggleWeekends = () => {
  showWeekends.value = !showWeekends.value;
};
const diets = ref([]);
const dietStore = useDietStore();
const calendarEvents = ref([]);

onMounted(async () => {
  await dietStore.getAllDiets();
  if (dietStore.dietByDateList.length === 0) {
    console.warn("오늘 식단이 없습니다.");
  }
  diets.value = [...dietStore.allDietList];
  console.log("오늘 식단:", diets.value);
  // 🔽 dietList를 vue-cal 일정 형식으로 매핑
  calendarEvents.value = diets.value.map((diet) => {
    const startDate = new Date(diet.mealDate); // "2025-05-23T00:00:00" → Date 객체
    const endDate = new Date(startDate);
    endDate.setHours(endDate.getHours() + 1); // 한 시간짜리 일정 (필수는 아님)

    return {
      start: startDate,
      end: endDate,
      title: `${diet.mealType} - ${diet.foods
        .map((f) => f.foodName)
        .join(", ")}`,
      class: "meal-event",
    };
  });
  await nextTick();
});
</script>

<template>
  <div>
    <button @click="toggleWeekends">Toggle Weekends</button>
    <vue-cal
      :hide-weekends="!showWeekends"
      :views="['month', 'day', 'week']"
      default-view="month"
      locale="ko"
      :events="calendarEvents"
      @view-change="(e) => console.log('view-change', e)"
    />
  </div>
</template>

<style>
.vuecal {
  height: 100%;
}

.meal-event {
  background-color: #f8caca;
  color: #000;
  margin: 10px;
  font-size: 30px;
  border-radius: 4px;
  padding: 2px 6px;
}
</style>
