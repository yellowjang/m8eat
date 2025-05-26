<script setup>
import { ref, onMounted, nextTick } from "vue";
import { VueCal } from "vue-cal";
import dayjs from "dayjs";
import { useDietStore } from "@/stores/diet";

const showWeekends = ref(true);
const toggleWeekends = () => {
  showWeekends.value = !showWeekends.value;
};
const emit = defineEmits(["select-date"]);
const diets = ref([]);
const dietStore = useDietStore();
const calendarEvents = ref([]);
const onCellClick = (e) => {
  const date = e.cursor?.date || e.cell?.start; // 🔍 날짜 위치에 따라 안전하게 꺼내기
  if (date) {
    const formattedDate = dayjs(date).format("YYYY-MM-DD");
    console.log("✅ 선택된 날짜:", formattedDate);
    emit("select-date", formattedDate); // 🔥 여기서 정확하게 전달!
  }
};
onMounted(async () => {
  await dietStore.getAllDiets();
  calendarEvents.value = dietStore.allDietList.map((diet) => {
    const start = new Date(diet.mealDate);
    const end = new Date(start);
    end.setHours(end.getHours() + 1);
    return {
      start,
      end,
      title: `${diet.mealType} - ${diet.foods
        .map((f) => f.foodName)
        .join(", ")}`,
      class: "meal-event",
    };
  });
});
</script>

<template>
  <div>
    <button @click="toggleWeekends">Toggle Weekends</button>
    <vue-cal
      :hide-weekends="!showWeekends"
      :views="['month', 'day', 'week']"
      default-view="month"
      events-on-month-view
      locale="ko"
      :events="calendarEvents"
      @view-change="(e) => console.log('view-change', e)"
      @cell-click="onCellClick"
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
