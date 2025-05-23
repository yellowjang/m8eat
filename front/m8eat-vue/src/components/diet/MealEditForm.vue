<template>
  <section class="meal-form">
    <p class="sub-title">식단 수정</p>
    <form @submit.prevent="handleSubmit">
      <!-- 이미지 업로드 -->
      <div class="image-upload-box">
        <div class="image-upload">
          <label class="input-title">이미지 업로드</label>
          <div class="image-upload-preview">
            <label v-if="!previewUrl" class="upload-placeholder">
              이미지 업로드
              <input type="file" @change="handleFileChange" accept="image/*" />
            </label>
            <div v-else class="image-preview">
              <img :src="previewUrl" alt="미리보기" />
              <button class="remove-button" type="button" @click="removeImage">
                <img class="remove" :src="deleteIcon" alt="제거" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 상세 시간 -->
      <div class="food-info">
        <p class="input-title">상세 시간</p>
        <VueDatePicker v-model="mealDate" :format="'yyyy-MM-dd'" />
        <div class="meal-type">
          <label>
            <input type="radio" value="아침" v-model="mealTime" />
            아침
          </label>
          <label>
            <input type="radio" value="점심" v-model="mealTime" />
            점심
          </label>
          <label>
            <input type="radio" value="저녁" v-model="mealTime" />
            저녁
          </label>
        </div>
      </div>

      <!-- 음식 목록 -->
      <div class="food-table">
        <p class="input-title">음식 목록</p>
        <ul class="food-list">
          <li v-for="(food, index) in foods" :key="index" class="food-item">
            {{ food.foodName }} / {{ food.amount }}g / {{ food.calorie }} kcal
            <button class="remove-button" type="button" @click="removeFood(index)">x</button>
          </li>
        </ul>
      </div>

      <div class="total-calories">총 칼로리: {{ totalCalories }} kcal</div>
      <div class="button-row">
        <button type="submit">수정 완료</button>
      </div>
    </form>
    <button class="back-button" @click="$emit('close')">← 뒤로가기</button>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useDietStore } from "@/stores/diet";
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import deleteIcon from "@/assets/icon/deleteIcon.png";

const route = useRoute();
const dietStore = useDietStore();
const dietNo = route.params.dietNo;
const props = defineProps({
  edit: Object,
});
const mealDate = ref(new Date());
const mealTime = ref("");
const foods = ref([]);
const file = ref(null);
const previewUrl = ref(null);

onMounted(() => {
  if (props.edit) {
    mealDate.value = new Date(props.edit.mealDate);
    mealTime.value = props.edit.mealType;
    foods.value = props.edit.foods || [];
    previewUrl.value = props.edit.filePath || null;
  }
});
const handleFileChange = (e) => {
  const selectedFile = e.target.files[0];
  if (selectedFile) {
    file.value = selectedFile;
    previewUrl.value = URL.createObjectURL(selectedFile);
  }
};

const removeImage = () => {
  file.value = null;
  previewUrl.value = null;
};

const removeFood = (index) => {
  foods.value.splice(index, 1);
};

const totalCalories = computed(() => foods.value.reduce((sum, food) => sum + food.calorie, 0));

const handleSubmit = async () => {
  const formData = new FormData();
  formData.append("dietNo", dietNo);
  formData.append("mealType", mealTime.value);
  formData.append("mealDate", mealDate.value.toISOString().slice(0, 10));
  formData.append("foods", JSON.stringify(foods.value));
  if (file.value) formData.append("file", file.value);

  await dietStore.updateDiet(formData);
  alert("식단이 성공적으로 수정되었습니다.");
  location.href = "/";
};
</script>

<style scoped>
/* 스타일은 MealForm에서 그대로 재사용 가능 */
</style>

<style scoped>
.meal-type {
  margin-top: 15px;
}
.sub-title {
  font-size: 20px;
  font-weight: 700;
}
.input-title {
  font-size: 16px;
  font-weight: 600;
  margin: 15px 0px 10px 0px;
}
.button-row {
  margin-top: 1rem;
}
.back-button {
  align-self: flex-start;
  background: none;
  border: none;
  color: #007aff;
  font-size: 14px;
  cursor: pointer;
}
.remove-button {
  border: none;
}
.remove-button:hover {
  border: none;
  background-color: transparent;
  color: #007aff;
}
</style>
