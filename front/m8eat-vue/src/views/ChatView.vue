<template>
  <div class="chat-container">
    <div class="chat-header">{{ targetUser }}님과의 채팅</div>

    <div class="chat-messages" ref="scrollRef">
      <template v-for="(msg, i) in messages" :key="i">
        <ChatMessage v-if="msg" :message="msg" :isMine="msg.sender === currentUser" />
      </template>

      <!-- <ChatMessage v-for="(msg, i) in messages" :key="i" :message="msg" :isMine="msg.sender === currentUser" /> -->
    </div>

    <ChatInput @send="(text) => sendMessage(text)" />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from "vue";
import { useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { useChatStore } from "@/stores/chat";
import { storeToRefs } from "pinia";

import ChatInput from "@/components/chat/ChatInput.vue";
import ChatMessage from "@/components/chat/ChatMessage.vue";

const route = useRoute();
const userStore = useUserStore();
const chatStore = useChatStore();

let currentUser = null;
chatStore.setCurrentUser(currentUser);

const targetUser = route.params.targetId;
const scrollRef = ref(null);

// const messages = ref([]);
const { messages } = storeToRefs(chatStore);
const roomId = ref(null); // ✅ 로컬 상태로 roomId 보관

watch(messages, () => {
  nextTick(() => {
    scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
  });
});

const loadChat = async () => {
  const result = await chatStore.loadRoomAndMessages(currentUser, targetUser);
  console.log("sfadsfsd", result);
  roomId.value = result.roomId;
  messages.value = result.messages;
  nextTick(() => {
    scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
  });
};

const sendMessage = (text) => {
  console.log("sendMessage", text);
  console.log("sendMessage", roomId.value);
  chatStore.sendMessage(roomId.value, userStore.loginUser.id, text);
};

onMounted(async () => {
  await userStore.checkLogin();
  currentUser = userStore.loginUser?.id;
  console.log("onMounted", currentUser)
  const result = await chatStore.loadRoomAndMessages(userStore.loginUser.id, targetUser);
  roomId.value = result.roomId;
  messages.value = result.messages;

  // ✅ WebSocket 연결은 데이터 로딩 이후에!
  chatStore.connectWebSocket(result.roomId);
});

// onMounted(() => {
//   loadChat();
// });

onBeforeUnmount(() => {
  chatStore.disconnectWebSocket();
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
