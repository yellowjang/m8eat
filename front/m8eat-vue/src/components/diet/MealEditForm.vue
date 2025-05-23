<template>
  <section class="meal-form">
    <p class="sub-title">식단 수정</p>
    <form @submit.prevent="handleSubmit">
      <div class="food-info">
        <p class="input-title">상세 시간</p>
        <VueDatePicker v-model="mealDate" :format="'yyyy-MM-dd'" />
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

      <div class="button-row">
        <button type="submit">식단 수정하기</button>
      </div>
    </form>
    <button class="back-button" @click="$emit('close')">← 뒤로가기</button>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import axios from "axios";

const emit = defineEmits(["close", "update-meal"]);
const props = defineProps({ edit: Object });

const mealDate = ref(new Date());
const mealTime = ref("");

onMounted(() => {
  if (props.edit) {
    mealDate.value = new Date(props.edit.mealDate);
    mealTime.value = props.edit.mealType;
  }
});

const handleSubmit = async () => {
  const formData = new FormData();
  formData.append("mealType", mealTime.value);
  formData.append("mealDate", mealDate.value.toISOString().slice(0, 10));
  formData.append("foods", JSON.stringify(props.edit.foods || []));
  formData.append("dietNo", props.edit.dietNo);
  formData.append("userNo", props.edit.userNo);

  try {
    await axios.put(`/api/diet/${props.edit.dietNo}`, formData);
    alert("식단이 성공적으로 수정되었습니다.");
    emit("update-meal");
  } catch (err) {
    alert("수정 실패");
  }
};
</script>

<style scoped>
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
</style>
