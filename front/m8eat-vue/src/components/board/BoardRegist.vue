<template>
  <div class="board-form">
    <h2 class="form-title">게시글 작성</h2>
    <form @submit.prevent="submitForm">
      <div class="form-group">
        <label for="title">제목</label>
        <input v-model.trim="title" type="text" id="title" required />
      </div>

      <div class="form-group">
        <label for="content">내용</label>
        <textarea v-model.trim="content" id="content" required></textarea>
      </div>

      <button type="submit" class="submit-btn">등록</button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { addBoard } from "@/api/board";

// const form = reactive({
//   title: "",
//   content: "",
// });

const title = ref('')
const content = ref('')

const submitForm = async () => {
  try {
    await addBoard();
    alert("게시글이 등록되었습니다.");
    // TODO: 성공 후 이동 (예: 게시글 목록)
  } catch (error) {
    console.error(error);
    alert("등록 중 오류가 발생했습니다.");
  }
};
import "@/style/boardForm.scss";
</script>

<style lang="scss" scoped></style>
