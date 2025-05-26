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
