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

<script setup></script>

<style lang="scss" scoped>
.main-content {
  padding: 2rem;
  //   max-width: 1000px;
  margin: 0 auto;
  font-family: "Noto Sans KR", sans-serif;
  background-color: #fdeeee;
  min-height: 100vh;

  h3 {
    font-size: 1.3rem;
    margin-bottom: 0.8rem;
  }

  .diet-record {
    margin-bottom: 2rem;

    .diet-box {
      height: 160px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    }
  }

  .community-columns {
    display: flex;
    gap: 1rem;

    .post-box {
      flex: 1;
      background: white;
      border-radius: 12px;
      padding: 1rem;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

      h4 {
        margin: 0.6rem 0;
      }

      ul {
        padding-left: 0;
        list-style: none;

        li {
          //   height: 1.2rem;
          background: #eee;
          margin: 0.4rem 0;
          border-radius: 4px;
          padding-left: 0.5rem;
        }
      }
    }
  }
}
</style>
