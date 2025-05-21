<template>
  <div class="container">
    <h2>🥗 식단 이미지 분석</h2>

    <input type="file" @change="onFileChange" />
    <button @click="analyzeImage" :disabled="!file || loading">분석 요청</button>

    <div v-if="loading">⏳ 분석 중...</div>
    <div v-if="error" class="error">❌ 분석 실패: {{ errorMessage }}</div>

    <div v-if="results.length > 0">
      <h3>📝 분석 결과:</h3>
      <div v-for="(item, idx) in results" :key="idx" class="result-item">
        <p><strong>영문 라벨:</strong> {{ item.label }}</p>
        <p><strong>번역 결과:</strong> {{ item.translated }}</p>
        <p><strong>매칭된 음식명:</strong> {{ item.matched }}</p>
        <div v-if="item.nutrition">
          <p><strong>칼로리:</strong> {{ item.nutrition.calorie }} kcal</p>
          <p><strong>단백질:</strong> {{ item.nutrition.protein }}g</p>
          <p><strong>지방:</strong> {{ item.nutrition.fat }}g</p>
        </div>
        <hr />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const file = ref(null)
const results = ref([])
const loading = ref(false)
const error = ref(false)
const errorMessage = ref('')

function onFileChange(e) {
  file.value = e.target.files[0]
  results.value = []
  error.value = false
}

async function analyzeImage() {
  if (!file.value) return
  loading.value = true
  error.value = false
  errorMessage.value = ''

  const formData = new FormData()
  formData.append('file', file.value)

  try {
    const res = await axios.post('http://localhost:8080/diets/api/image/label', formData)
    results.value = res.data
  } catch (err) {
    console.error('분석 실패:', err)
    error.value = true
    errorMessage.value = err.response?.data || '서버 오류'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.container {
  max-width: 600px;
  margin: 2rem auto;
}
.error {
  color: red;
  margin-top: 1rem;
}
.result-item {
  background: #f9f9f9;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}
</style>
