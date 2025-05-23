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
import { useBoardStore } from "@/stores/board"
import { useUserStore } from "@/stores/user"

const route = useRoute();
const router = useRouter();

const boardStore = useBoardStore();
const userStore = useUserStore();

const board = ref({});

const requestBoardDetail = async () => {
  try {
    const boardNo = route.params.boardNo;
    board.value = await boardStore.getBoardDetail(boardNo);
  } catch {
    alert("요청을 처리하지 못했습니다.")
    router.push({name: "boardList"})
  }
};

const requestBoardList = () => {
  router.push({ name: "boardList" });
};

const requestBoardDelete = async () => {
  try {
    const boardNo = route.params.boardNo;
    await boardStore.removeBoard(boardNo);
    alert("삭제되었습니다.")
    router.push({ name: "boardList" });
  } catch {
    alert("요청을 처리하지 못했습니다.")
  }
};

requestBoardDetail();
import "@/style/Board.scss";
</script>

<style scoped></style>
