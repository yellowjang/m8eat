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
        <!-- TODO : 내용 출력 필요 -->
      </div>
      <div class="detail-actions">
        <!-- TODO : 목록, 수정, 삭제 버튼 클릭 시 이벤트 처리 필요 -->
        <button class="btn" @click="requestBoardList">목록</button>
        <button class="btn btn-danger" @click="requestBoardDelete">삭제</button>
        <button class="btn btn-secondary">수정</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getBoardDetail, removeBoard } from "@/api/board";
const route = useRoute();
const router = useRouter();

const board = ref({});

const requestBoardDetail = async () => {
  const boardNo = route.params.boardNo;
  board.value = await getBoardDetail(boardNo);
};

const requestBoardList = () => {
  router.push({ name: "boardList" });
};
const requestBoardDelete = async () => {
  const boardNo = route.params.boardNo;
  await removeBoard(boardNo);
  router.push({ name: "boardList" });
};

requestBoardDetail();
import "@/style/Board.scss";
</script>

<style scoped></style>
