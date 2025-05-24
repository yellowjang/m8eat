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

      <!-- ✅ 파일 첨부 -->
      <div class="form-group">
        <label for="file">파일 첨부</label>
        <input type="file" id="file" @change="onFileChange" />
      </div>

      <button type="submit" class="submit-btn">등록</button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { addBoard } from "@/api/board";
import { useBoardStore } from "@/stores/board";
import { useRouter } from "vue-router";

const store = useBoardStore();
const router = useRouter();

// const form = reactive({
//   title: "",
//   content: "",
// });

const title = ref("");
const content = ref("");
const file = ref(null);

const onFileChange = (event) => {
  file.value = event.target.files[0];
};

const submitForm = async () => {
  try {
    const formData = new FormData();
    formData.append("title", title.value);
    formData.append("content", content.value);

    if (file.value) {
      formData.append("file", file.value);
    }

    await store.addBoard(formData);
    alert("게시글을 등록하였습니다.");
    // alert("게시글이 등록되었습니다.");
    // TODO: 성공 후 이동 (예: 게시글 목록)
  } catch (error) {
    console.error(error);
    alert("등록 중 오류가 발생했습니다.");
  }
  router.push({ name: "boardList" });
};
import "@/style/boardForm.scss";
</script>

<style lang="scss" scoped></style>
