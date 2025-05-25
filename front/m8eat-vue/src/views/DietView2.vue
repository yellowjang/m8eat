<template>
  <div>
    <Modal v-if="selectedMeal === 'create'" @close="resetSelection">
      <MealForm @close="resetSelection" @add-meal="refreshMeals" />
    </Modal>
    <Modal
      v-if="selectedMeal && selectedMeal !== 'create'"
      @close="resetSelection"
    >
      <MealEditForm
        :edit="selectedMeal"
        @close="resetSelection"
        @update-meal="refreshMeals"
      />
    </Modal>
    <MealToday
      v-if="!selectedMeal"
      @add-meal="selectedMeal = 'create'"
      @edit-meal="handleEditMeal"
    />
  </div>
  <Calendar />
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import MealToday from "@/components/diet/MealToday.vue";
import MealForm from "@/components/diet/MealForm.vue";
import MealEditForm from "@/components/diet/MealEditForm.vue";
import Modal from "@/components/common2/Modal.vue";
import { useDietStore } from "@/stores/diet";
import Calendar from "@/components/diet/Calendar.vue";
const selectedMeal = ref(null);
const dietStore = useDietStore();
const route = useRoute();

const refreshMeals = () => {
  selectedMeal.value = null;
};

const resetSelection = () => {
  selectedMeal.value = null;
};

const fetchDetail = async () => {
  const dietNo = route.params.dietNo;
  if (dietNo) {
    await dietStore.getDietDetail(dietNo);
    selectedMeal.value = JSON.parse(JSON.stringify(dietStore.diet));
  }
};

const handleEditMeal = async (meal) => {
  await dietStore.getDietDetail(meal.dietNo);
  selectedMeal.value = JSON.parse(JSON.stringify(dietStore.diet));
};

onMounted(fetchDetail);
// onMounted(async () => {
//   await getAllDiets();
// });
const props = defineProps({
  edit: Object,
});

const mealDate = ref(null);
const mealTime = ref("");
const foods = ref([]);
const file = ref(null);
const previewUrl = ref(null);
watch(
  () => props.edit,
  (newVal) => {
    if (newVal) {
      mealDate.value = new Date(newVal.mealDate);
      mealTime.value = newVal.mealType;
      foods.value = [...newVal.foods];
      previewUrl.value = newVal.filePath || null;
    }
  },
  { immediate: true } // mount 시 바로 실행
);
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
