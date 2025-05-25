<!-- ChatMessage.vue -->
<template>
  <div :class="['chat-message', isMine ? 'mine' : 'theirs']">
    <div class="bubble">{{ message.content }}</div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

const props = defineProps({
  message: {
    type: Object,
    required: true,
    validator: (val) => typeof val === "object" && val.content !== undefined,
  },
  isMine: Boolean,
});

// const message = computed(() => props.message);
// const message = ref(props.message);
// const { message, isMine } = defineProps();

// console.log("🧾 ChatMessage.vue에서 받은 message:", props.message);
console.log("🧾 ChatMessage.vue에서 받은 message:", props.message);
</script>

<style scoped>
.chat-message {
  display: flex;
}
.chat-message.mine {
  justify-content: flex-end;
}
.chat-message.theirs {
  justify-content: flex-start;
}
.bubble {
  padding: 0.5rem 1rem;
  background: #eee;
  border-radius: 20px;
  max-width: 60%;
}
.chat-message.mine .bubble {
  background: #dcf8c6;
}
</style>
