<template>
  <div>
    <SelectUserType v-if="!userType" @select="userType = $event" />
    <SignupForm v-else :userType="userType" />
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import SelectUserType from "@/components/user/SelectUserType.vue";
import SignupForm from "@/components/user/SignupForm.vue";
import { useRoute } from "vue-router";

const route = useRoute();

const userType = ref(route.params.role || null); // 'user' 또는 'coach'
watch(
  () => route.params.role,
  (newVal) => {
    userType.value = newVal || null;
  }
);

console.log("UserType ", userType.value);
</script>

<style scoped>
/* 필요 시 여기에 뷰 전체 스타일 작성 */
</style>
