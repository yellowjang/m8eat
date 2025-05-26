<template>
  <section class="meal-form">
    <p class="sub-title">{{ props.edit ? "식단 수정" : "식단 등록" }}</p>
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
              <button type="button" class="remove-image-button" @click="removeImage">
                <img class="remove" :src="deleteIcon" alt="제거" />
              </button>
            </div>
          </div>
        </div>
        <!-- <div class="analysis-result-box">
          <p class="input-title">분석 결과</p>
          <p>분석한 결과가 여기에 나올 거예요.</p>
        </div> -->
        <div class="analysis-result-box">
          <p class="input-title">분석 결과</p>

          <div v-if="loading">⏳ 분석 중입니다...</div>
          <div v-else-if="results.length === 0">분석한 결과가 여기에 나올 거예요.</div>
          <!-- <ul v-else>
            <li v-for="(item, idx) in results" :key="idx">
              🍽️ 라벨: {{ item.label }}
              <br />
              🇰🇷 번역: {{ item.translated }}
              <br />
              🔍 매칭: {{ item.matched }}
              <br />
              <span v-if="item.nutrition">🔥 칼로리: {{ item.nutrition.calories }} kcal</span>
              <span v-else>⚠️ 영양 정보 없음</span>
            </li>
          </ul> -->
          <!-- <ul v-else>
            <li v-for="(item, idx) in results" :key="idx" @click="selectAnalyzedItem(item)" class="analysis-result-item" style="cursor: pointer; padding: 0.5rem; border-radius: 6px">
              🔍 매칭: {{ item.matched }}
              <br />
              <span v-if="item.nutrition">🔥 칼로리: {{ item.nutrition.calories }} kcal</span>
              <span v-else>⚠️ 영양 정보 없음</span>
            </li>
          </ul> -->
          <ul v-else>
            <li v-for="(item, idx) in results" :key="idx" @click="selectAnalyzedItem(item)" class="analysis-result-item" style="cursor: pointer; padding: 0.5rem; border-radius: 6px">
              🔍 매칭: {{ item.matched }}
              <br />
              <span v-if="item.nutrition">🔥 칼로리: {{ item.nutrition.calories }} kcal</span>
              <span v-else>⚠️ 영양 정보 없음</span>
            </li>
          </ul>
        </div>
      </div>

      <!-- 상세 시간 -->
      <div class="food-info">
        <p class="input-title">상세 시간</p>
        <VueDatePicker v-model="mealDate" :enable-time-picker="true" format="yyyy-MM-dd HH:mm" :minute-increment="5" />
        <div class="meal-type">
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
      </div>

      <!-- 음식 입력 -->
      <div class="food-table">
        <p class="input-title">음식 입력</p>

        <div class="food-row">
          <input type="text" placeholder="음식명" v-model="foodInput" @input="filterFoodList" @blur="confirmSelectedFood" list="food-suggestions" />
          <datalist id="food-suggestions">
            <option v-for="food in filteredFoods" :key="food.foodId" :value="food.nameKo" />
          </datalist>
          <input type="number" placeholder="g" v-model.number="foodAmount" @input="calculateCalories" />
          <input type="number" placeholder="kcal" v-model.number="foodCalories" :readonly="!!selectedFood" />
          <button class="add-button" type="button" @click="addFood">추가</button>
        </div>

        <!-- 음식 리스트 -->
        <ul class="food-list">
          <li v-for="(food, index) in foods" :key="index" class="food-item">
            {{ food.foodName }} / {{ food.amount }}g / {{ food.calorie }} kcal
            <button type="button" @click="removeFood(index)">x</button>
          </li>
        </ul>
      </div>

      <div class="total-calories">총 칼로리: {{ totalCalories }} kcal</div>
      <div class="button-row">
        <button type="submit">
          {{ props.edit ? "식단 수정하기" : "식단 추가하기" }}
        </button>
      </div>
    </form>
    <button class="back-button" @click="$emit('close')">← 뒤로가기</button>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import deleteIcon from "@/assets/icon/deleteIcon.png";
import { useDietStore } from "@/stores/diet";
import { useFoodStore } from "@/stores/food";
import dayjs from "dayjs";

import api from "@/api";
import axios from "axios";

const results = ref([]); // 분석 결과 저장
const loading = ref(false); // 로딩 상태

const emit = defineEmits(["close"]);

const dietStore = useDietStore();
const foodStore = useFoodStore();

const mealDate = ref(new Date());
const mealTime = ref("");
const foodInput = ref("");
const foodAmount = ref(null);
const foodCalories = ref(null);
const foods = ref([]);
const file = ref(null);
const previewUrl = ref(null);

const selectedFood = ref(null);
const filteredFoods = ref([]);
const props = defineProps({
  edit: Object, // null이면 새로 등록
});
onMounted(() => {
  foodStore.fetchFoods();
  if (props.edit) {
    mealDate.value = new Date(props.edit.mealDate);
    mealTime.value = props.edit.mealType;
    foods.value = props.edit.foods;
  }
});
if (props.edit && props.edit.filePath) {
  previewUrl.value = props.edit.filePath;
}
// const handleFileChange = (e) => {
//   const selectedFile = e.target.files[0];
//   if (selectedFile) {
//     file.value = selectedFile;
//     previewUrl.value = URL.createObjectURL(selectedFile);
//   }
// };
const handleFileChange = async (e) => {
  const selected = e.target.files[0];
  if (selected) {
    file.value = selected;
    previewUrl.value = URL.createObjectURL(selected);

    // 이미지 분석 요청
    const formData = new FormData();
    formData.append("file", selected);
    loading.value = true;

    console.log("handlefilechangeeeeeeeeeeee", selected);

    try {
      const res = await api.post("http://localhost:8080/diets/ai/vision-gpt", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      results.value = res.data;
    } catch (err) {
      console.error("분석 실패:", err);
      // alert("분석 실패: " + err.message);
    } finally {
      loading.value = false;
    }
  }
};

// const selectAnalyzedItem = (item) => {
//   foodInput.value = item.matched;
//   foodCalories.value = item.nutrition?.calories ?? null;
//   foodAmount.value = null; // 사용자 직접 입력 유도
//   selectedFood.value = null; // 자동 매핑 X
// };

const selectAnalyzedItem = (item) => {
  foodInput.value = item.matched;

  const matchedFood = foodStore.foods.find((f) => f.nameKo === item.matched);

  if (matchedFood && item.nutrition?.calories) {
    selectedFood.value = matchedFood;

    // 100g당 kcal 정보가 있는 경우 → g 역산해서 넣기
    const caloriesPer100g = matchedFood.calories;
    const targetCalories = item.nutrition.calories;
    const estimatedGrams = Math.round((targetCalories / caloriesPer100g) * 100);

    foodAmount.value = estimatedGrams;
    foodCalories.value = targetCalories;
  } else {
    // 영양 정보 없거나 매칭 안 되는 경우 수동 입력 유도
    selectedFood.value = null;
    foodAmount.value = null;
    foodCalories.value = item.nutrition?.calories ?? null;
  }
};

// const calculateCalories = () => {
//   if (!foodAmount.value) return;
//   if (selectedFood.value) {
//     const ratio = foodAmount.value / 100;
//     foodCalories.value = Math.round(selectedFood.value.calories * ratio);
//   }
// };

const removeImage = () => {
  file.value = null;
  previewUrl.value = null;
};

const filterFoodList = () => {
  const query = foodInput.value.trim().toLowerCase();
  filteredFoods.value = foodStore.foods.filter((food) => food.nameKo.toLowerCase().includes(query));
};

const confirmSelectedFood = () => {
  selectedFood.value = foodStore.foods.find((f) => f.nameKo.trim() === foodInput.value.trim()) || null;
  calculateCalories();
};

const calculateCalories = () => {
  if (!foodAmount.value) return;
  if (selectedFood.value) {
    const ratio = foodAmount.value / 100;
    foodCalories.value = Math.round(selectedFood.value.calories * ratio);
  }
};

const addFood = () => {
  if (!foodInput.value || !foodAmount.value || !foodCalories.value) return;

  const food = {
    foodId: selectedFood.value?.foodId ?? 0,
    foodName: foodInput.value,
    amount: foodAmount.value,
    calorie: foodCalories.value,
    protein: selectedFood.value ? selectedFood.value.protein * (foodAmount.value / 100) : 0,
    fat: selectedFood.value ? selectedFood.value.fat * (foodAmount.value / 100) : 0,
    carbohydrate: selectedFood.value ? selectedFood.value.carbohydrate * (foodAmount.value / 100) : 0,
    sugar: selectedFood.value ? selectedFood.value.sugar * (foodAmount.value / 100) : 0,
    cholesterol: selectedFood.value ? selectedFood.value.cholesterol * (foodAmount.value / 100) : 0,
  };

  foods.value.push(food);
  console.log("addddd", foods.value);

  foodInput.value = "";
  foodAmount.value = null;
  foodCalories.value = null;
  selectedFood.value = null;
};

const removeFood = (index) => {
  foods.value.splice(index, 1);
};

const totalCalories = computed(() => foods.value.reduce((sum, food) => sum + food.calorie, 0));

// const handleSubmit = async () => {
//   const formData = new FormData();
//   formData.append("mealType", mealTime.value);
//   formData.append("mealDate", mealDate.value.toISOString().slice(0, 10));
//   if (file.value) {
//     formData.append("file", file.value);
//   }
//   formData.append("foods", JSON.stringify(foods.value));
//   try {
//     await dietStore.createDiet(formData);
//     alert("식단이 성공적으로 등록되었습니다.");
//   } catch (error) {
//     console.error("식단 등록 실패:", error);
//     alert("식단 등록에 실패했습니다.");
//   }
// };
const handleSubmit = async () => {
  const formData = new FormData();
  formData.append("mealType", mealTime.value);
  console.log("1231213123", formData.get("mealType"));

  const formattedDate = dayjs(mealDate.value).format("YYYY-MM-DD HH:mm");
  formData.append("mealDate", formattedDate);

  formData.append("foods", JSON.stringify(foods.value));
  if (file.value) formData.append("file", file.value);

  if (props.edit) {
    formData.append("dietNo", props.edit.dietNo);
    formData.append("userNo", props.edit.userNo);
    await axios.put(`/api/diet/${props.edit.dietNo}`, formData);
    alert("식단이 성공적으로 수정되었습니다.");

    emit("update-meal");
  } else {
    console.log("handleee", formData);
    await dietStore.createDiet(formData);
    alert("식단이 성공적으로 등록되었습니다.");
    emit("add-meal");
  }
};
</script>

<style scoped>
.analysis-result-item:hover {
  background-color: #f1f1f1;
}
.meal-type {
  margin-top: 15px;
}

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
  margin-top: 10px;
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
.remove-image-button img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.remove-image-button {
  z-index: 1;
  position: absolute;
  top: 8px;
  right: 8px;
  background: transparent;
  border: none;
  cursor: pointer;
  width: 24px;
  height: 24px;
}
</style>
