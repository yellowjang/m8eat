<template>
  <div class="coach-dashboard p-4">
    <h2 class="text-2xl font-bold mb-4">코치 대시보드</h2>

    <!-- 내 회원 목록 -->
    <section>
      <h3 class="text-xl font-semibold mb-2">담당 회원 목록</h3>
      <ul>
        <li v-for="member in members" :key="member.userNo" @click="selectMember(member.userNo)" class="cursor-pointer hover:text-blue-600 mb-1">{{ member.name }} ({{ member.id }})</li>
      </ul>
    </section>

    <!-- 선택한 회원의 식단 -->
    <section v-if="selectedMember">
      <h3 class="text-xl font-semibold mt-6 mb-2">{{ selectedMemberName }}님의 식단 기록</h3>
      <ul>
        <li v-for="diet in diets" :key="diet.dietNo" class="mb-1">{{ diet.mealDate }} - {{ diet.mealType }}</li>
      </ul>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useCoachStore } from "@/stores/coach";

const members = ref([]);
const selectedMember = ref(null);
const selectedMemberName = ref("");
const diets = ref([]);

const coachStore = useCoachStore();

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
  } catch (err) {
    alert("식단 정보 불러오기 실패");
  }
};

onMounted(() => {
  fetchMembers();
});
</script>

<style scoped>
.coach-dashboard {
  max-width: 700px;
  margin: 0 auto;
}
</style>
