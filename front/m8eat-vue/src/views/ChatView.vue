<!-- ChatView.vue -->
<template>
  <div class="chat-container">
    <div class="chat-header">{{ targetUser }}님과의 채팅</div>

    <div class="chat-messages" ref="scrollRef">
      <ChatMessage v-for="(msg, i) in messages" :key="i" :message="msg" :isMine="msg.sender === currentUser" />
    </div>

    <ChatInput @send="sendMessage" />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { useChatStore } from "@/stores/chat";

import ChatMessage from "@/components/chat/ChatMessage.vue";
import ChatInput from "@/components/chat/ChatInput.vue";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const chatStore = useChatStore();

const currentUser = userStore.loginUser?.id;
chatStore.setCurrentUser(currentUser);

const targetUser = route.params.targetId;
const scrollRef = ref(null);

const messages = ref([]);
const roomId = ref(null);

const loadChat = async () => {
  try {
    const result = await chatStore.loadRoomAndMessages(currentUser, targetUser);
    console.log("뭔데에에ㅔ게에", result.messages);
    roomId.value = result.roomId;
    messages.value = result.messages;
    nextTick(() => {
      scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
    });
  } catch (e) {
    console.error("채팅 로딩 실패:", e);
  }
};

const sendMessage = async (text) => {
  try {
    const message = await chatStore.sendMessage(roomId.value, currentUser, text);
    console.log("sendMessage ", messages.value);
    messages.value.push(message);
    nextTick(() => {
      scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
    });
  } catch (e) {
    console.error("메시지 전송 실패:", e);
  }
};

onMounted(() => {
  if (!currentUser) {
    console.warn("로그인 정보 없음");
    router.push({ name: "login" }); // ✅ 로그인 페이지로 리다이렉트
    return;
  }
  loadChat();
});
</script>

<style scoped>
.chat-container {
  max-width: 600px;
  margin: auto;
  height: 80vh;
  display: flex;
  flex-direction: column;
  border: 1px solid #ddd;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
}
.chat-header {
  padding: 1rem;
  font-weight: bold;
  background-color: #f2f2f2;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
</style>
