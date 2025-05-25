<script setup>
import { RouterLink, RouterView } from "vue-router";
import Header from "@/components/common2/Header.vue";
import "@/style/global.scss";
import "vue-cal/style.css";
</script>

<template>
  <Header></Header>
  <div class="view">
    <RouterView />
  </div>
</template>

<style>
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

.view {
  padding: 5rem;
}
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
