<template>
  <main class="meal-record-container">
    <!-- 식단 카드 -->
    <section class="meal-card">
      <div class="meal-content">
        <h3>🍱 오늘의 식단</h3>
        <div class="meal-boxes">
          <div class="meal-time">
            <label>아침</label>
            <div class="meal-input">내용</div>
          </div>
          <div class="meal-time">
            <label>점심</label>
            <div class="meal-input">내용</div>
          </div>
          <div class="meal-time">
            <label>저녁</label>
            <div class="meal-input">내용</div>
          </div>
        </div>
      </div>

      <!-- 중간: 달력 자리 -->
      <div class="calendar-area">
        <p style="text-align: center">📅 달력</p>
      </div>

      <!-- 오른쪽: 체크 아이콘 -->
      <!-- <div class="check-icon">✅</div> -->
    </section>

    <!-- 커뮤니티 -->
    <section class="community">
      <h3>커뮤니티</h3>
      <div class="community-columns">
        <div class="post-box">
          <h4>인기글</h4>
          <ul>
            <li v-for="i in 5" :key="i"></li>
          </ul>
        </div>
        <div class="post-box">
          <h4>최신글</h4>
          <ul>
            <li v-for="i in 5" :key="'new' + i"></li>
          </ul>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup></script>

<style lang="scss" scoped>
.meal-record-container {
  padding: 2rem;
  max-width: 1000px;
  margin: 0 auto;
  background-color: #fdeeee;

  h3 {
    font-size: 1.3rem;
    margin-bottom: 1rem;
  }

  .meal-card {
    background: white;
    border-radius: 16px;
    padding: 1.5rem;
    margin-bottom: 2rem;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
    display: flex;
    justify-content: space-around;

    .meal-content {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
      flex-wrap: wrap;

      .meal-boxes {
        display: flex;
        gap: 1rem;

        .meal-time {
          display: flex;
          flex-direction: column;
          align-items: center;

          label {
            font-weight: bold;
            margin-bottom: 0.5rem;
          }

          .meal-input {
            width: 100px;
            height: 80px;
            background: #f5f5f5;
            border-radius: 8px;
          }
        }
      }

      //   .check-icon {
      //     font-size: 1.5rem;
      //     margin-right: 1rem;
      //   }
    }
    .calendar-area {
      background: #f5f5f5;
      border-radius: 12px;
      padding: 1rem;
      width: 200px;
      //   height: 160px;
    }
  }

  .community {
    .community-columns {
      display: flex;
      gap: 1rem;

      .post-box {
        flex: 1;
        background: white;
        border-radius: 12px;
        padding: 1rem;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

        ul {
          list-style: none;
          padding: 0;
          margin-top: 0.5rem;

          li {
            background: #e0e0e0;
            height: 1.2rem;
            border-radius: 4px;
            margin: 0.4rem 0;
          }
        }
      }
    }
  }
}
</style>
