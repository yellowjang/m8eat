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
        <tr v-for="board in boards" :key="board.boardNo">
          <template v-if="board && board.boardNo">
            <td>{{ board.boardNo }}</td>
            <td>
              <RouterLink :to="{ name: 'boardDetail', params: { boardNo: board.boardNo } }">{{ board.title }}</RouterLink>
            </td>
            <td>{{ board.userNo }}</td>
            <td>{{ board.viewCnt }}</td>
          </template>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getBoardList } from "@/api/board";
import { useUserStore } from "@/stores/user";
const store = useUserStore();

const router = useRouter();
const boards = ref([]);

// watch(() => store.loginUser, (val) => {
//   if (val === null) {
//     router.push({ name: 'login' });
//   }
// });


const requestBoardList = async () => {
  boards.value = await getBoardList();
  console.log(boards.value)
  console.log("보드리스트 불러옴!");
};

const moveToBoardForm = () => {
  router.push({ name: "boardRegist" });
};
// requestBoardList();

onMounted(() => {
  if (store.loginUser) {
    requestBoardList();
  }
});
</script>

<style lang="scss" scoped></style>
