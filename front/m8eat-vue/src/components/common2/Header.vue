<template>
  <header class="app-header">
    <RouterLink to="/" class="logo">
      <img src="@/assets/img/logo.png" alt="로고" />
    </RouterLink>
    <div class="header-left">
      <RouterLink to="/">Home</RouterLink>
      <RouterLink to="/boards">자유게시판</RouterLink>
    </div>
    <div class="header-right">
      <template v-if="isLoggedIn">
        <span class="greeting">{{ userName }}님, 건강한 하루 되세요!</span>

        <div class="profile-dropdown" @click="toggleDropdown">
          <img class="profile-img" :src="profileImageUrl" alt="프로필" />
          <ul v-if="dropdownOpen" class="dropdown-menu">
            <li @click="goToMyPage">마이페이지</li>
            <li @click="logout">로그아웃</li>
          </ul>
        </div>
      </template>

      <template v-else>
        <RouterLink to="/login" class="nav-btn">로그인</RouterLink>
        <RouterLink to="/signup" class="nav-btn">회원가입</RouterLink>
      </template>
    </div>
  </header>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

// 로그인 여부 및 사용자 정보
const isLoggedIn = ref(false); // 실제론 store 또는 auth composable 사용
const userName = ref("홍길동");
const profileImageUrl = ref("https://via.placeholder.com/40");

const dropdownOpen = ref(false);
const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value;
};

const router = useRouter();

const goToMyPage = () => {
  router.push("/mypage");
};

const logout = () => {
  // TODO: 로그아웃 로직 구현
  alert("로그아웃 되었습니다.");
  isLoggedIn.value = false;
  dropdownOpen.value = false;
};
import "@/style/header.scss";
</script>
