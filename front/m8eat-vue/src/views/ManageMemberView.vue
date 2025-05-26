<template>
  <div class="coach-dashboard bg-pink-50 min-h-screen p-6">
    <!-- 오늘의 식단 + 회원 목록 -->
    <div class="bg-white rounded-2xl shadow p-6 flex justify-between gap-6">
      <div class="w-1/3">
        <h2 class="title">회원 목록</h2>
        <ul class="space-y-2">
          <li
            v-for="member in members"
            :key="member.userNo"
            class="flex justify-between items-center bg-gray-50 px-4 py-2 rounded cursor-pointer hover:bg-gray-100"
          >
            <span>{{ member.name }}</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- 회원 카드 목록 -->
    <section class="mt-10">
      <h2 class="title">회원 관리</h2>
      <div class="profile-boxes">
        <div v-for="member in members" :key="member.userNo" class="profile-box">
          <div class="img-info">
            <img alt="프로필" class="" />
            <div class="flex-1">
              <p class="name">{{ member.name }}</p>
              <p class="text-sm text-gray-500">26세 / 여</p>
            </div>
          </div>
          <div class="button-box">
            <button
              @click="selectMember(member.userNo)"
              class="text-sm text-gray-600 underline"
            >
              식단조회
            </button>
            <button class="text-sm text-blue-500">💬 채팅</button>
            <button class="text-sm text-gray-500">⚙️</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 선택한 회원의 식단 목록 -->
    <section v-if="selectedMember" class="mt-10">
      <h3 class="sub-title">{{ selectedMemberName }}님의 식단 목록</h3>
      <ul class="space-y-1">
        <li
          class="diets-list"
          v-for="diet in diets"
          :key="diet.dietNo"
          @click="selectDietDetail(diet.dietNo)"
        >
          <div class="list">
            <span>{{ diet.dietNo }}</span>
            <span>{{ formatDateTime(diet.mealDate) }}</span>
          </div>
          <!-- <div class="divide-line"></div> -->
          <hr />
        </li>
      </ul>

      <!-- 선택된 식단 상세 -->
      <div v-if="dietDetail" class="diet-detail">
        <p><strong>식사 종류:</strong> {{ dietDetail.mealType }}</p>
        <p><strong>식단 날짜:</strong> {{ dietDetail.mealDate }}</p>
        <p><strong>등록 시간:</strong> {{ dietDetail.regDate }}</p>

        <div class="image-box" v-if="dietDetail.filePath">
          <img
            :src="dietDetail.filePath"
            alt="식단 이미지"
            class="diet-image"
          />
        </div>

        <h3 class="text-lg font-semibold mt-4">음식 목록</h3>
        <ul class="food-list">
          <li v-for="(food, idx) in dietDetail.foods" :key="idx">
            <p>
              <strong>{{ food.foodName }}</strong>
            </p>
            <p>섭취량: {{ food.amount }}g</p>
            <p>열량: {{ food.calorie }} kcal</p>
            <p>
              탄: {{ food.carbohydrate }}g | 단: {{ food.protein }}g | 지:
              {{ food.fat }}g
            </p>
            <p>당: {{ food.sugar }}g | 콜레스테롤: {{ food.cholesterol }}mg</p>
          </li>
        </ul>

        <h3 class="text-lg font-semibold mt-6">총 영양 성분</h3>
        <NutrientBarChart :data="totalNutrients" />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useCoachStore } from "@/stores/coach";
import { useDietStore } from "@/stores/diet";
import dayjs from "dayjs";
import NutrientBarChart from "@/components/diet/NutrientBarChart.vue";
const members = ref([]);
const selectedMember = ref(null);
const selectedMemberName = ref("");
const diets = ref([]);
const formatDateTime = (dateStr) => {
  return dayjs(dateStr).format("YYYY-MM-DD HH:mm");
};

const coachStore = useCoachStore();
const dietStore = useDietStore();
const dietDetail = ref(null);
const totalNutrients = computed(() => {
  return dietDetail.value?.foods?.reduce(
    (acc, food) => {
      acc.carbohydrate += food.carbohydrate || 0;
      acc.protein += food.protein || 0;
      acc.fat += food.fat || 0;
      acc.sugar += food.sugar || 0;
      acc.cholesterol += food.cholesterol || 0;
      return acc;
    },
    { carbohydrate: 0, protein: 0, fat: 0, sugar: 0, cholesterol: 0 }
  );
});
const fetchMembers = async () => {
  try {
    members.value = await coachStore.fetchMembers();
  } catch (err) {
    alert("회원 목록 불러오기 실패");
  }
};

const selectMember = async (userNo) => {
  selectedMember.value = userNo;
  const member = members.value.find((m) => m.userNo === userNo);
  selectedMemberName.value = member ? member.name : "";

  try {
    diets.value = await coachStore.fetchMemberDiets(userNo);
    console.log(diets.value);
    dietDetail.value = null;
  } catch (err) {
    alert("식단 정보 불러오기 실패");
  }
};

const selectDietDetail = async (dietNo) => {
  try {
    await dietStore.getDietDetail(dietNo);
    dietDetail.value = dietStore.dietDetail;
  } catch (err) {
    alert("식단 상세 조회 실패");
  }
};

onMounted(() => {
  fetchMembers();
});
</script>

<style scoped>
.title {
  font-size: 30px;
  font-weight: 600;
  margin-bottom: 20px;
}
.sub-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
}
li {
  list-style: none;
}
.coach-dashboard {
  font-family: "Noto Sans KR", sans-serif;
}

.button-box {
  display: flex;
  flex-direction: row;
  justify-content: center;
}
.button-box button {
  border: none;
}
img {
  border-radius: 50%;
  width: 50px;
  height: 50px;
  border: 0.5px solid #ccc;
}
.name {
  font-size: 18px;
  font-weight: 600;
}
.img-info {
  display: flex;
  gap: 20px;
  flex-direction: row;
}
.diets-list {
  display: flex;
  align-items: center;
  /* text-align: center; */
  /* justify-content: center; */
  height: 30px;
  font-size: 20px;
  border-bottom: 0.5px solid #ccc;
  /* background-color: #ffffff; */
  border-radius: 7px;
  height: 40px;
}

.list {
  display: flex;
  flex-direction: row;
  gap: 40px;
}
.profile-boxes {
  width: 100;
  padding: 20px;
  display: flex;
  flex-direction: row;
  justify-content: center;
  /* background-color: #fff; */
  width: 100%;
  height: 250px;
  margin: auto;
  gap: 20px;
  align-items: center;
  overflow: scroll;
}
.profile-box {
  gap: 20px;
  width: 250px;
  height: 170px;
  background-color: #fff;
  border: 0.5px solid #ccc;
  border-radius: 7px;
  box-shadow: 5px 5px 5px #ede4e4;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.diet-detail {
  padding: 1rem;
  background: #fff;
  border-radius: 10px;
  box-shadow: 2px 2px 5px #ddd;
  margin-top: 1rem;
}
.diet-image {
  max-width: 100%;
  border-radius: 8px;
}
.food-list {
  list-style: none;
  padding: 0;
}
.food-list li {
  border-top: 1px solid #eee;
  padding: 0.75rem 0;
}
.image-box {
  margin: 1rem 0;
}
</style>
