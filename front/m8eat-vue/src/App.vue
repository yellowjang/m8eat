<script setup>
import { RouterLink, RouterView } from "vue-router";
import Header from "@/components/common2/Header.vue";
import Footer from "./components/common2/Footer.vue";
import "@/style/global.scss";
import { onMounted } from "vue";
import { useUserStore } from "@/stores/user";

const store = useUserStore();

onMounted(async () => {
  try {
    await store.checkLogin(); // ✅ 로그인 상태 복구 시도
    console.log("✅ 로그인 상태 복구됨")
  } catch (err) {
    console.log("❌ 로그인 아님")
  }
})
import "vue-cal/style.css";
</script>

<template>
  <div id="app">
    <Header></Header>
    <div class="view">
      <RouterView />
    </div>
    <Footer></Footer>
  </div>
</template>

<style scoped>
html,
body {
  height: 100%;
  margin: 0;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.view {
  flex: 1;
  padding: 3rem 1rem 2rem; /* ✅ 아래 padding 줄이기 */
}
.vuecal__title-bar {
  display: flex;
  /* justify-content: center; */
  margin: auto;
}
.vuecal__weekdays-headings {
  background-color: #fddede !important; /* 상단 배경색 */
  color: #a13a3a !important; /* 요일 글자색 */
  font-weight: bold;
}
.vuecal__header {
  background-color: #de9c9c !important;
}
.vuecal__heading {
  background-color: #ffe6e6 !important; /* 월 이름이 있는 헤더 */
  /* color: #c94e4e !important; */
  font-size: 16px;
}
.vuecal__body {
  height: 500px;
}
.vuecal__scrollable--month-view .vuecal__cell-date {
  font-size: 11px;
  margin: 1px;
  width: 1.7em;
}
.vuecal__body-wrap {
  overflow: hidden;
}
.vuecal__body {
  aspect-ratio: 13 / 9;
  overflow: auto;
}
.vuecal__cell {
  overflow: auto;
}
.vuecal__event {
  padding: 0 2px;
}
.vuecal__event {
  height: auto !important;
  min-height: 20px !important;
  padding: 4px 6px;
  background-color: #fff5f5;
  border: 1px solid #de9c9c;
  font-size: 12px;
  color: #333;
  box-sizing: border-box;
}

.vuecal__cell-events {
  display: flex !important;
  flex-direction: column !important;
  gap: 4px;
}
.vuecal__event.meal-event {
  background-color: #fdf3f3;
  border-left: 4px solid #f08f8f;
  color: #333;
}
header {
  line-height: 1.5;
  max-height: 100vh;
}

/* .view {
  padding: 5rem;
} */
.logo {
  display: block;
  margin: 0 auto 2rem;
}

nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  nav {
    text-align: left;
    margin-left: -1rem;
    font-size: 1rem;

    padding: 1rem 0;
    margin-top: 1rem;
  }
}
</style>
