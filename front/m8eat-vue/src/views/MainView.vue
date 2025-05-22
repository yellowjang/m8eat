<template>
  <main class="main-content">
    <section class="diet-record">
      <h3>식단 기록</h3>
      <div class="diet-box"></div>
    </section>

    <section class="community">
      <h3>커뮤니티</h3>
      <div class="community-columns">
        <div class="post-box">
          <h4>인기글 🔥</h4>
          <ul>
            <li v-for="n in 5" :key="n">게시글 {{ n }}</li>
          </ul>
        </div>
        <div class="post-box">
          <h4>최신글 ⭐</h4>
          <ul>
            <li v-for="n in 5" :key="'new-' + n">게시글 {{ n }}</li>
          </ul>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup>
import "@/style/main.scss";
</script>
<style></style>
