<template>
  <div>
    <div class="board-header">
      <h2>게시판 목록</h2>
      <button @click="moveToBoardForm">등록</button>
    </div>

    <table class="board-table">
      <thead>
        <tr>
          <th class="id-column">번호</th>
          <th class="title-column">제목</th>
          <th class="writer-column">작성자</th>
          <th class="view-column">조회수</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="board in boards" :key="board.id">
          <td>{{ board.boardNo }}</td>
          <td>
            <RouterLink :to="{ name: 'boardDetail', params: { boardNo: board.boardNo } }">{{ board.title }}</RouterLink>
          </td>
          <td>{{ board.userNo }}</td>
          <td>{{ board.viewCnt }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { getBoardList } from "@/api/board";

const router = useRouter();
const boards = ref([]);

const requestBoardList = async () => {
  boards.value = await getBoardList();
  console.log("보드리스트 불러옴!");
};

const moveToBoardForm = () => {
  router.push({ name: "boardRegist" });
};
requestBoardList();
</script>

<style lang="scss" scoped></style>
