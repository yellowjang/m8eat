<!-- src/components/common/Modal.vue -->
<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <button class="modal-close" @click="close">×</button>
      <slot />
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(["close"]);
const close = () => {
  emit("close");
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 2rem;
  border-radius: 10px;
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
  width: 90%;
  max-width: 700px;
}

.modal-close {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 1.5rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #888;
}
</style>
