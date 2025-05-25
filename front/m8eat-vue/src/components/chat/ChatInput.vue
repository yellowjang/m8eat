<!-- ChatInput.vue -->
<!-- <template>
  <form class="chat-input" @submit.prevent="submit">
    <input v-model.trim="text" type="text" placeholder="메시지를 입력하세요" />
    <button type="submit">전송</button>
  </form>
</template>

<script setup>
import { ref } from "vue";

const emit = defineEmits(["send"]);
const text = ref("");

const submit = () => {
  if (text.value) {
    console.log("chatinput text: ", text.value);
    emit("send", text.value);
    text.value = "";
  }
};
</script> -->

<template>
  <form class="chat-input" @submit.prevent="submit">
    <input v-model.trim="text" type="text" placeholder="메시지를 입력하세요" />
    <button type="submit">전송</button>
  </form>
</template>

<script setup>
import { ref } from "vue";
const emit = defineEmits(["send"]);
const text = ref("");

const submit = () => {
  console.log("textttt", text.value);
  if (text?.value) {
    emit("send", text.value);
    text.value = "";
  }
};
</script>

<style scoped>
.chat-input {
  display: flex;
  padding: 0.5rem;
  border-top: 1px solid #ddd;
  background: #f9f9f9;
}
.chat-input input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.chat-input button {
  margin-left: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
