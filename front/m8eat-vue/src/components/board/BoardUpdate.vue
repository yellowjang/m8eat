<template>
  <div class="board-form">
    <h2 class="form-title">게시글 수정</h2>
    <form @submit.prevent="submitForm">
      <div class="form-group">
        <label for="title">제목</label>
        <input v-model.trim="title" type="text" id="title" required />
      </div>

      <div class="form-group">
        <label for="content">내용</label>
        <textarea v-model.trim="content" id="content" required></textarea>
      </div>

      <!-- 기존 파일 정보 표시 -->
      <div class="form-group" v-if="board.filePath">
        <label>기존 첨부파일:</label>
        <span>{{ getFileName(board.filePath) }}</span>
      </div>

      <!-- 새 파일 선택 -->
      <div class="form-group">
        <label for="file">새 파일 첨부 (선택)</label>
        <input type="file" id="file" @change="onFileChange" />
      </div>

      <button type="submit" class="submit-btn">수정</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useBoardStore } from "@/stores/board";

const route = useRoute();
const router = useRouter();
const store = useBoardStore();

const board = ref({});
const title = ref("");
const content = ref("");
const file = ref(null);

const onFileChange = (event) => {
  file.value = event.target.files[0];
};

const getFileName = (filePath) => {
  return filePath.split("/").pop();
};

// ✅ 게시글 불러오기
const getBoard = async () => {
  const boardNo = route.params.boardNo;
  board.value = await store.getBoardDetail(boardNo);
  title.value = board.value.title;
  content.value = board.value.content;
};

// ✅ 게시글 수정 요청
const submitForm = async () => {
  try {
    const formData = new FormData();
    formData.append("boardNo", board.value.boardNo); // 서버에서 필요하면
    formData.append("title", title.value);
    formData.append("content", content.value);
    // if (file.value) {
    //   formData.append("file", file.value);
    // }

    // 기존 파일 유지 정보도 함께 보냄
    formData.append("filePath", board.value.filePath || "");

    if (file.value) {
      formData.append("file", file.value); // 새 파일 선택된 경우에만
    }

    await store.updateBoard(formData, board.value.boardNo); // store에 updateBoard 정의 필요
    alert("게시글이 수정되었습니다.");
    router.push({ name: "boardDetail", params: { boardNo: board.value.boardNo } });
  } catch (error) {
    console.error(error);
    alert("수정 중 오류가 발생했습니다.");
  }
};

onMounted(() => {
  getBoard();
});
import "@/style/boardForm.scss";
</script>
