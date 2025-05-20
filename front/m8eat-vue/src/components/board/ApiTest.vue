<template>
  <div>
    <input type="file" @change="onFileChange" />
    <button @click="uploadImage">분석 요청</button>

    <div v-if="result.length">
      <h4>분석 결과:</h4>
      <ul>
        <li v-for="item in result" :key="item.description">{{ item.description }} (신뢰도: {{ (item.score * 100).toFixed(1) }}%)</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";

const file = ref(null);
const result = ref([]);

function onFileChange(e) {
  file.value = e.target.files[0];
}

async function uploadImage() {
  const formData = new FormData();
  formData.append("file", file.value);

  try {
    const res = await axios.post("http://localhost:8080/diets/api/image/label", formData);
    result.value = res.data;
  } catch (err) {
    console.error("업로드 실패:", err);
  }
}
</script>
