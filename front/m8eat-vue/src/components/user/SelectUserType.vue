<template>
  <div class="container">
    <h2 class="title">어떤 회원으로 시작하고 싶나요?</h2>
    <div class="card-wrapper">
      <div class="card" @click="goSignup('coach')">
        <img src="@/assets/coach.png" alt="코치" class="icon" />
        <h3 class="type">코치</h3>
        <p class="text">회원들의 식단을 점검하고 관리할 수 있습니다.</p>
        <small class="note">※ 인증 과정 필요</small>
      </div>
      <div class="card" @click="goSignup('user')">
        <img src="@/assets/user.png" alt="일반 회원" class="icon" />
        <h3 class="type">일반 회원</h3>
        <p class="text">식단을 기록하고, 코치와 소통할 수 있습니다.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

const goSignup = (type) => {
  router.push({ name: "signup", params: { role: type } });
};
</script>

<style scoped>
.container {
  text-align: center;
  padding: 3rem 1rem;
  background-color: #ffeef1;
  /* min-height: 100vh; */
}
.title {
  font-size: 2rem;
  margin-bottom: 2rem;
}
.card-wrapper {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
}
.card {
  background: white;
  border-radius: 15px;
  padding: 2rem;
  width: 400px;
  height: 400px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}
.card:hover {
  transform: translateY(-5px);
}
.icon {
  width: 200px;
  height: 200px;
  margin-bottom: 1rem;
}

.type {
  font-size: 24px;
}
.text {
  margin-top: 10px;
  font-size: 18px;
}
.note {
  margin-top: 12px;
  color: #c0392b;
  font-size: 1rem;
}
</style>
