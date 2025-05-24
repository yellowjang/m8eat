import { defineStore } from "pinia";
import { ref } from "vue";
import api from "@/api";

const REST_API_URL = `http://localhost:8080/api/chat`;

export const useChatStore = defineStore("chat", () => {
  const currentUser = ref(null); // 로그인 사용자 ID

  const setCurrentUser = (userId) => {
    currentUser.value = userId;
  };

  const loadRoomAndMessages = async (user1, user2) => {
    try {
      console.log("loadRoomAndMessages :", user1, user2);
      const roomRes = await api.get(`${REST_API_URL}/room`, {
        params: { user1, user2 },
      });
      const roomId = roomRes.data.id;

      const msgRes = await api.get(`${REST_API_URL}/room/${roomId}/messages`);
      console.log("loadRoomAndMessages msgRes ", msgRes.data);
      const messages = msgRes.data;

      return { roomId, messages };
    } catch (error) {
      console.error("채팅방 및 메시지 로딩 실패", error);
      throw error;
    }
  };

  const sendMessage = async (roomId, sender, content) => {
    try {
      const payload = { roomId, sender, content };
      const res = await api.post(`${REST_API_URL}/message`, payload);
      return res.data;
      // return { ...payload, sentAt: new Date().toISOString() };
    } catch (error) {
      console.error("메시지 전송 실패", error);
      throw error;
    }
  };

  return {
    currentUser,
    setCurrentUser,
    loadRoomAndMessages,
    sendMessage,
  };
});
