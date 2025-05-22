<template>
  <section class="meal-form">
    <p class="sub-title">식단 등록</p>

    <form @submit.prevent="handleSubmit">
      <!-- 이미지 업로드 -->
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
              <!-- 엑스 버튼 이미지 사용 -->
              <button type="button" class="remove-image-button" @click="removeImage">
                <img src="@/assets/icon/X.svg" alt="제거" />
              </button>
            </div>
          </div>
        </div>

        <!-- 분석 결과 -->
        <div class="analysis-result-box">
          <p class="input-title">분석 결과</p>
          <p>분석한 결과가 여기에 나올 거예요.</p>
          <p>분석한 결과가 여기에 나올 거예요.</p>
        </div>
      </div>
      <!-- 상세 시간 -->
      <div class="food-info">
        <p class="input-title">상세 시간</p>
        <label>
          <input type="radio" name="meal" value="아침" v-model="mealTime" />
          아침
        </label>
        <label>
          <input type="radio" name="meal" value="점심" v-model="mealTime" />
          점심
        </label>
        <label>
          <input type="radio" name="meal" value="저녁" v-model="mealTime" />
          저녁
        </label>
      </div>

      <!-- 음식 입력 -->
      <div class="food-table">
        <p class="input-title">음식 입력</p>
        <div class="food-row">
          <input type="text" placeholder="음식명" v-model="foodName" />
          <input type="number" placeholder="g" v-model.number="foodAmount" />
          <input type="number" placeholder="kcal" v-model.number="foodCalories" />
          <button class="add-button" type="button" @click="addFood">추가</button>
        </div>

        <!-- 음식 리스트 -->
        <ul class="food-list">
          <li v-for="(food, index) in foods" :key="index" class="food-item">
            {{ food.name }} / {{ food.amount }}g / {{ food.kcal }} kcal
            <button type="button" @click="removeFood(index)">x</button>
          </li>
        </ul>
      </div>

      <!-- 총 칼로리 -->
      <div class="total-calories">총 칼로리: {{ totalCalories }} kcal</div>

      <!-- 제출 버튼 -->
      <div class="button-row">
        <button type="submit">식단 추가하기</button>
      </div>
    </form>
    <button class="back-button" @click="$emit('close')">← 뒤로가기</button>
  </section>
</template>

<script setup>
import { ref, computed } from "vue";

const emit = defineEmits(["close"]);

const mealTime = ref("");
const foodName = ref("");
const foodAmount = ref(null);
const foodCalories = ref(null);
const foods = ref([]);

const file = ref(null);
const previewUrl = ref(null);

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

const addFood = () => {
  if (!foodName.value || !foodAmount.value || !foodCalories.value) return;
  foods.value.push({
    name: foodName.value,
    amount: foodAmount.value,
    kcal: foodCalories.value,
  });
  foodName.value = "";
  foodAmount.value = null;
  foodCalories.value = null;
};

const removeFood = (index) => {
  foods.value.splice(index, 1);
};

const totalCalories = computed(() => foods.value.reduce((sum, food) => sum + food.kcal, 0));

const handleSubmit = () => {
  console.log({
    mealTime: mealTime.value,
    foods: foods.value,
    totalCalories: totalCalories.value,
    file: file.value,
  });
};
</script>

<style scoped>
.add-button {
  width: 60px;
}
.analysis-result-box {
  display: flex;
  flex-direction: column;
  .input-title {
    margin-top: 0;
  }
}
.input-title {
  font-size: 16px;
  font-weight: 600;
  margin: 15px 0px 10px 0px;
}

.sub-title {
  font-size: 20px;
  font-weight: 700;
}
.meal-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.food-row {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}
.food-list {
  margin-top: 1rem;
  list-style: none;
  padding: 0;
}
.food-item {
  display: flex;
  justify-content: space-between;
  background: #f9f9f9;
  padding: 0.5rem;
  border-radius: 6px;
  margin-bottom: 0.5rem;
}
.food-item button {
  background: none;
  border: none;
  color: red;
  cursor: pointer;
}
.total-calories {
  margin-top: 1rem;
  font-weight: 600;
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
.image-upload-box {
  display: flex;
  flex-direction: row;
  gap: 30px;
}
.image-upload-preview {
  width: 250px;
  height: 250px;
  position: relative;
}

.upload-placeholder {
  display: flex;
  width: 250px;
  height: 250px;
  border: 2px dashed #ccc;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #888;
  cursor: pointer;
  text-align: center;
}
.upload-placeholder input {
  display: none;
}

.image-preview {
  width: 250px;
  height: 250px;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image-button {
  position: absolute;
  top: -10px;
  right: -10px;
  background: transparent;
  border: none;
  cursor: pointer;
  width: 24px;
  height: 24px;
}
.remove-image-button img {
  width: 24px;
  height: 24px;
}
</style>
