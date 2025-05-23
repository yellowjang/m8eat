<template>
  <div>
    <MealForm v-if="selectedMeal === 'create'" @close="resetSelection" @add-meal="refreshMeals" />
    <MealEditForm v-else-if="selectedMeal && selectedMeal !== 'create'" :edit="selectedMeal" @close="resetSelection" @update-meal="refreshMeals" />
    <MealToday v-else @add-meal="selectedMeal = 'create'" @edit-meal="selectedMeal = $event" />
  </div>
</template>

<script setup>
import { ref } from "vue";
import MealToday from "@/components/diet/MealToday.vue";
import MealForm from "@/components/diet/MealForm.vue";
import MealEditForm from "@/components/diet/MealEditForm.vue";

const selectedMeal = ref(null);

const refreshMeals = () => {
  selectedMeal.value = null;
};

const resetSelection = () => {
  selectedMeal.value = null;
};
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
