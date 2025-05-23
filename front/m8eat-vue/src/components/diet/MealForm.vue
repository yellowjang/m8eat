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
        <div class="analysis-result-box">
          <p class="input-title">분석 결과</p>
          <p>분석한 결과가 여기에 나올 거예요.</p>
        </div>
      </div>

      <!-- 상세 시간 -->
      <div class="food-info">
        <p class="input-title">상세 시간</p>
        <VueDatePicker v-model="mealDate" :format="'yyyy-MM-dd'" />
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
        <button type="submit">{{ props.edit ? "식단 수정하기" : "식단 추가하기" }}</button>
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
  formData.append("mealDate", mealDate.value.toISOString().slice(0, 10));
  formData.append("foods", JSON.stringify(foods.value));
  if (file.value) formData.append("file", file.value);

  if (props.edit) {
    formData.append("dietNo", props.edit.dietNo);
    formData.append("userNo", props.edit.userNo);
    await axios.put(`/api/diet/${props.edit.dietNo}`, formData);
    alert("식단이 성공적으로 수정되었습니다.");

    emit("update-meal");
  } else {
    await dietStore.createDiet(formData);
    alert("식단이 성공적으로 등록되었습니다.");
    emit("add-meal");
  }
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
