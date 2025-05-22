<template>
  <div>
    <h2>🍱 Vision + GPT 분석 테스트</h2>
    <input type="file" @change="onFileChange" />
    <button @click="analyzeImage">분석 요청</button>

    <div v-if="loading">⏳ 분석 중입니다...</div>

    <div v-if="results.length > 0">
      <h3>📦 분석 결과</h3>
      <p v-if="results.length === 0">❗결과가 없습니다.</p>

      <ul v-else>
        <li v-for="(item, idx) in results" :key="idx">
          🍽️ 라벨: {{ item.label }}
          <br>🇰🇷 번역: {{ item.translated }}
          <br>🔍 매칭: {{ item.matched }}
          <div v-if="item.nutrition">
            🔥 칼로리: {{ item.nutrition.calories }}kcal
          </div>
          <div v-else>
            ⚠️ 영양 정보 없음
          </div>
        </li>
      </ul>


    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const selectedFile = ref(null)
const loading = ref(false)
const results = ref([])

const onFileChange = (e) => {
  selectedFile.value = e.target.files[0]
}

const analyzeImage = async () => {
  if (!selectedFile.value) return alert("파일을 선택하세요")
  loading.value = true
  const formData = new FormData()
  formData.append("file", selectedFile.value)

  try {
    const res = await axios.post("http://localhost:8080/diets/ai/vision-gpt", formData)
    results.value = res.data
  } catch (err) {
    console.error(err)
    alert("분석 실패: " + err.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
h2 {
  margin-bottom: 1rem;
}
ul {
  margin-bottom: 1rem;
}
</style>
