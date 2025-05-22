<template>
  <div class="meal-record">
    <!-- 좌측 영역 -->
    <div class="left-panel">
      <!-- 오늘의 식단 -->
      <section class="meal-today">
        <h2>오늘의 식단</h2>
        <div class="meal-boxes">
          <div class="meal-box">
            <p class="meal-title">아침</p>
            <!-- for 문 돌리기-->
            <ul class="food-list">
              <li class="food-name">{{ 음식명 }}음식이름</li>
              <li class="food-name">{{ 음식명 }}음식이름</li>
              <li class="food-name">{{ 음식명 }}음식이름</li>
            </ul>
            <div>
              <p class="calorie">총 섭취 칼로리 : {{}} kcal</p>
            </div>
          </div>
          <div class="meal-box">
            <p class="meal-title">점심</p>
            <!-- for 문 돌리기-->
            <ul class="food-list">
              <li class="food-name">{{ 음식명 }}음식이름</li>
              <li class="food-name">{{ 음식명 }}음식이름</li>
              <li class="food-name">{{ 음식명 }}음식이름</li>
            </ul>
            <div>
              <p class="calorie">총 섭취 칼로리 : {{}} kcal</p>
            </div>
          </div>
          <div class="meal-box">
            <p class="meal-title">저녁</p>
            <!-- for 문 돌리기-->
            <ul class="food-list">
              <li class="food-name">{{ 음식명 }}음식이름</li>
              <li class="food-name">{{ 음식명 }}음식이름</li>
              <li class="food-name">{{ 음식명 }}음식이름</li>
            </ul>
            <div>
              <p class="calorie">총 섭취 칼로리 : {{}} kcal</p>
            </div>
          </div>
        </div>
        <p class="summary">총 섭취 칼로리: 724 kcal</p>
        <div class="add-meal-box">
          <a href="#" class="add-meal">+ 식단 추가하기</a>
        </div>
      </section>

      <!-- 식단 분석 -->
      <section class="meal-analysis">
        <h2>식단 분석</h2>
        <div class="calorie-chart">
          <!-- 원형 차트 대신 텍스트 예시 -->
          <div class="circle">
            <p class="left-kcal">860</p>
            <p>남은 칼로리</p>
          </div>
        </div>
        <div class="nutrient-bars">
          <h3>오늘의 영양소</h3>
          <!-- 바 그래프 대체 막대 -->
          <div>바 차트 넣기</div>
          <div class="bar">탄수화물</div>
          <div class="bar">단백질</div>
          <div class="bar">지방</div>
          <div class="bar">기타</div>
        </div>
        <div class="calorie-summary">
          <p>
            섭취 칼로리:
            <strong>1240</strong>
          </p>
          <p>
            목표 칼로리:
            <strong>2100</strong>
          </p>
        </div>
      </section>
    </div>

    <!-- 우측 영역 -->
    <div class="right-panel">
      <section class="meal-calendar">
        <div class="calendar-header">
          <h2>식단 캘린더</h2>
        </div>

        <!-- 달력 -->
        <div class="calendar">
          <p>📅 2025년 5월 (달력 자리)</p>
        </div>

        <!-- 통계 -->
        <div class="calendar-summary">
          <h3>5월 식단 통계</h3>
          <p>
            평균 일일 칼로리:
            <strong>1,620 kcal</strong>
          </p>
          <p>
            최소 칼로리:
            <strong class="min">1,240 kcal</strong>
          </p>
          <p>
            최대 칼로리:
            <strong class="max">2,150 kcal</strong>
          </p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
// 추후 필요시 setup logic 작성 가능
</script>

<style lang="scss" scoped>
.meal-record {
  display: flex;
  gap: 2rem;
  padding: 2rem;
  background-color: #fcecec;

  .left-panel,
  .right-panel {
    flex: 1;
    background: white;
    padding: 2rem;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  }

  h2 {
    font-size: 1.5rem;
    margin-bottom: 1rem;
  }

  .meal-boxes {
    display: flex;
    gap: 1rem;

    .meal-box {
      flex: 1;
      background: #fff5f5;
      padding: 1rem;
      border-radius: 8px;
      text-align: center;
      font-weight: bold;
    }
  }
  .meal-title {
    font-size: 16px;
    font-weight: 700;
  }
  .food-list {
    margin: 15px 10px;
  }
  .food-name {
    font-size: 12px;
    font-weight: 400;
  }
  .calorie {
    font-size: 12px;
    font-weight: 500;
    color: #d26767;
  }
  .summary {
    margin-top: 1rem;
    font-weight: 700;
    font-size: 16px;
    color: #4e4949;
  }

  .add-meal {
    text-align: right;
    margin-left: auto;
    margin-top: 0.5rem;
    font-size: 14px;
    color: #de9c9c;
    font-weight: 600;
  }
  .add-meal-box {
    display: flex;
  }
  .meal-analysis {
    margin-top: 3rem;

    .circle {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      border: 10px solid #de9c9c;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      margin: auto;

      .left-kcal {
        font-size: 1.5rem;
        font-weight: bold;
      }
    }

    .nutrient-bars {
      margin-top: 2rem;

      .bar {
        height: 20px;
        background: #ffe6e6;
        margin-bottom: 0.5rem;
        border-radius: 4px;
        padding-left: 0.5rem;
      }
    }

    .calorie-summary {
      margin-top: 1.5rem;

      strong {
        font-weight: bold;
      }
    }
  }

  .calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .greeting {
      font-size: 0.9rem;
      color: #999;
    }
  }

  .calendar {
    margin-top: 1.5rem;
    background: #fff8f8;
    border-radius: 8px;
    height: 500px;
    padding: 1rem;
    text-align: center;
  }

  .calendar-summary {
    margin-top: 2rem;

    .min {
      color: green;
    }

    .max {
      color: red;
    }
  }
}
</style>
