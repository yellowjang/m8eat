<template>
  <footer class="app-footer">
    <div class="footer-content">
      <div class="footer-left">
        <img src="@/assets/img/log.svg" alt="로고" class="footer-logo" />
        <p>© 2025 M8eat. All rights reserved.</p>
      </div>

      <div class="footer-links">
        <RouterLink to="/">홈</RouterLink>
        <RouterLink to="/about">서비스 소개</RouterLink>
        <RouterLink to="/policy">이용약관</RouterLink>
        <RouterLink to="/privacy">개인정보처리방침</RouterLink>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { RouterLink } from "vue-router";
// import "@/style/footer.scss";
</script>

<style lang="scss" scoped>
.app-footer {
  background-color: #f5f5f5;
  height: 70px;
  border-top: 1px solid #ddd;
  font-size: 0.85rem;
  color: #555;

  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    height: 100%;
    display: flex;
    justify-content: space-between; // 양쪽 정렬
    align-items: center; // 수직 가운데 정렬
    padding: 0 1rem;
  }

  .footer-left {
    display: flex;
    align-items: center;
    gap: 1rem;

    .footer-logo {
      height: 40px;
    }

    p {
      margin: 0;
    }
  }

  .footer-links {
    display: flex;
    gap: 1rem;

    a {
      color: #666;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
        color: #333;
      }
    }
  }
}
</style>
