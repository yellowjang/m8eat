<template>
  <div class="comment-section">
    <h3>댓글</h3>

    <!-- 댓글 작성 폼 -->
    <div class="comment-form">
      <textarea v-model="newComment" placeholder="댓글을 입력하세요..." />
      <button @click="handleSubmit">등록</button>
    </div>

    <!-- 댓글 목록 -->
    <ul class="comment-list">
      <li v-for="comment in comments" :key="comment.commentNo" class="comment-item">
        <div class="comment-header">
          <span class="comment-author">{{ comment.userName || "익명" }}</span>
          <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
        </div>

        <!-- 수정 모드 -->
        <div v-if="editingCommentId === comment.commentNo">
          <textarea v-model="editContent" />
          <button @click="update(comment.commentNo)">저장</button>
          <button @click="cancelEdit">취소</button>
        </div>

        <!-- 일반 보기 모드 -->
        <div v-else class="comment-body">
          <p>{{ comment.content }}</p>
          <div class="flex-box">
            <div v-if="comment.userNo === userStore.loginUser?.userNo" class="comment-actions">
              <button @click="edit(comment)">수정</button>
              <button @click="remove(comment.commentNo)">삭제</button>
            </div>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute } from "vue-router";
import { useBoardStore } from "@/stores/board";
import { useUserStore } from "@/stores/user";

const props = defineProps({
  comments: Array,
});

const boardStore = useBoardStore();
const userStore = useUserStore();
const route = useRoute();

const boardNo = route.params.boardNo;

const newComment = ref("");
const editingCommentId = ref(null);
const editContent = ref("");

const handleSubmit = async () => {
  if (!newComment.value || newComment.value.trim() === "") {
    alert("댓글 내용을 입력해주세요.");
    return;
  }

  console.log("📦 등록할 댓글:", newComment.value);

  try {
    await boardStore.writeComment(boardNo, {
      userNo: userStore.loginUser.userNo,
      content: newComment.value,
    });
    newComment.value = "";
    await boardStore.getComments(boardNo); // 새로고침
  } catch (e) {
    alert("댓글 등록 실패");
  }
};
const edit = (comment) => {
  editingCommentId.value = comment.commentNo;
  editContent.value = comment.content;
};

const cancelEdit = () => {
  editingCommentId.value = null;
  editContent.value = "";
};

const update = async (commentNo) => {
  try {
    await boardStore.updateComment(boardNo, commentNo, editContent.value);
    editingCommentId.value = null;
    editContent.value = "";
  } catch (e) {
    alert("댓글 수정 실패");
  }
};

const remove = async (commentNo) => {
  if (!confirm("정말 삭제하시겠습니까?")) return;
  try {
    await boardStore.deleteComment(boardNo, commentNo);
  } catch (e) {
    alert("댓글 삭제 실패");
  }
};

const formatDate = (dateStr) => {
  return dateStr ? new Date(dateStr).toLocaleString() : "";
};
</script>

<style scoped>
.comment-section {
  margin-top: 2rem;
  background-color: #f9f9f9;
  padding: 1rem;
  border-radius: 8px;
}
.comment-form textarea {
  margin-top: 10px;
  width: 100%;
  height: 60px;
  margin-bottom: 0.5rem;
}
.comment-form button {
  float: right;
  background-color: #de9c9c;
  color: white;
  border: none;
  padding: 0.4rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}
.flex-box {
  display: flex;
}
.comment-list {
  margin-top: 40px;
  list-style: none;
  padding: 0;
}
.comment-item {
  background: white;
  margin-bottom: 1rem;
  padding: 0.7rem 1rem;
  border-radius: 6px;
}
.comment-header {
  font-size: 0.9rem;
  color: #666;
  display: flex;
  justify-content: space-between;
}
.comment-body {
  margin-top: 0.4rem;
}
.comment-actions {
  margin-left: auto;
}
.comment-actions button {
  margin-right: 0.5rem;
  background: none;
  color: #888;
  border: none;
  cursor: pointer;
  font-size: 0.85rem;
}
.comment-actions button:hover {
  color: #de9c9c;
}
</style>
