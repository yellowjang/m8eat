<template>
  <div>
    <h2>게시글 상세</h2>
    <div class="detail-container">
      <div class="detail-header">
        <h1 class="detail-title">{{ board.title }}</h1>
        <div class="detail-info">
          <!-- TODO : 작성자, 조회수, 작성일 출력 필요 -->
          <!-- <h2>제목 : {{ board.title }}</h2> -->
          <span>{{ board.viewCnt }}</span>
          <span>{{ board.regDate }}</span>
          <!-- <span>{{ board.userNo }}</span> -->
        </div>
      </div>
      <div class="detail-content">
        {{ board.content }}
        <!-- ✅ 파일이 있는 경우 -->
        <div v-if="board.filePath" class="file-preview">
          <!-- 이미지인 경우 -->
          <img v-if="isImage(board.filePath)" :src="getImageUrl(board.filePath)" alt="첨부 이미지" />

          <!-- 이미지가 아닌 경우 -->
          <div v-else class="file-download">
            <span class="file-name">{{ getFileName(board.filePath) }}</span>
            <a :href="getImageUrl(board.filePath)" download target="_blank" class="download-icon" title="다운로드">📎</a>
          </div>

          <!-- 이미지가 아닌 경우 -->
          <!-- <div v-else class="file-download">
            <p>첨부 파일: {{ getFileName(board.filePath) }}</p>
            <a :href="getImageUrl(board.filePath)" download target="_blank" class="download-link">다운로드</a>
          </div> -->
        </div>
      </div>
      <div class="detail-actions">
        <!-- TODO : 목록, 수정, 삭제 버튼 클릭 시 이벤트 처리 필요 -->
        <button class="btn" @click="requestBoardList">목록</button>
        <button class="btn btn-danger" @click="requestBoardDelete" v-if="board.userNo === userStore.loginUser?.userNo">삭제</button>
        <button class="btn btn-secondary">수정</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getBoardDetail, removeBoard } from "@/api/board";
import { useBoardStore } from "@/stores/board";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const router = useRouter();

const boardStore = useBoardStore();
const userStore = useUserStore();

const board = ref({});

const requestBoardDetail = async () => {
  try {
    const boardNo = route.params.boardNo;
    board.value = await boardStore.getBoardDetail(boardNo);
    console.log(board.value);
  } catch {
    alert("요청을 처리하지 못했습니다.");
    router.push({ name: "boardList" });
  }
};

const requestBoardList = () => {
  router.push({ name: "boardList" });
};

const requestBoardDelete = async () => {
  try {
    const boardNo = route.params.boardNo;
    await boardStore.removeBoard(boardNo);
    alert("삭제되었습니다.");
    router.push({ name: "boardList" });
  } catch {
    alert("요청을 처리하지 못했습니다.");
  }
};

const getImageUrl = (filePath) => {
  return `http://localhost:8080${filePath}`;
};

// ✅ 확장자를 기준으로 이미지인지 판단
const isImage = (filePath) => {
  return /\.(png|jpe?g|gif|bmp|webp)$/i.test(filePath);
};

// ✅ 파일명만 추출 (예: /upload/a.csv → a.csv)
const getFileName = (filePath) => {
  return filePath.split("/").pop();
};
requestBoardDetail();
import "@/style/Board.scss";
</script>

<style scoped></style>
