import { defineStore } from "pinia";
import { ref } from "vue";
import api from "@/api";

const REST_API_URL = `http://localhost:8080/api/chat`;

export const useChatStore = defineStore("chat", () => {
  const currentUser = ref(null); // 로그인 사용자 ID
  const socket = ref(null);
  const messages = ref([]);
  const roomId = ref(null); // ✅ 현재 연결된 roomId 저장

  const setCurrentUser = (userId) => {
    currentUser.value = userId;
  };

  const loadRoomAndMessages = async (user1, user2) => {
    try {
      currentUser.value = user1;
      console.log("lodadddd", user1, user2)
      const roomRes = await api.get(`${REST_API_URL}/room`, {
        params: { user1, user2 },
      });
      roomId.value = roomRes.data.id;

      const msgRes = await api.get(`${REST_API_URL}/room/${roomId.value}/messages`);
      messages.value = msgRes.data;

      return { roomId: roomId.value, messages: messages.value };
    } catch (error) {
      console.error("❌ 채팅방 및 메시지 로딩 실패", error);
      throw error;
    }
  };

  const connectWebSocket = (roomIdValue) => {
    const ws = new WebSocket("ws://localhost:8080/ws/chat");
    socket.value = ws;

    ws.onopen = () => {
      console.log("✅ WebSocket 연결됨");

      // ✅ currentUser, roomId 체크 후 join 메시지 전송
      if (!roomIdValue || !currentUser.value) {
        console.warn("⚠️ roomId 또는 currentUser가 비어있음", roomIdValue, currentUser.value);
        return;
      }

      ws.send(
        JSON.stringify({
          type: "join",
          roomId: roomIdValue,
          sender: currentUser.value,
        })
      );
    };

    ws.onmessage = (event) => {
      const message = JSON.parse(event.data);
      console.log("💬 수신한 메시지:", message);

      // 해당 방의 메시지만 반영
      if (message.roomId === roomId.value) {
        messages.value = [...messages.value, message];
      }
    };

    ws.onclose = () => {
      console.log("❌ WebSocket 연결 종료");
    };
  };

  const sendMessage = (roomIdValue, sender, content) => {
    if (socket.value && socket.value.readyState === WebSocket.OPEN) {
      const msg = {
        roomId: roomIdValue,
        sender,
        content,
      };
      socket.value.send(JSON.stringify(msg));
    } else {
      console.warn("⚠️ WebSocket이 아직 연결되지 않았습니다.");
    }
  };

  const disconnectWebSocket = () => {
    if (socket.value) {
      socket.value.close();
      socket.value = null;
    }
  };

  return {
    currentUser,
    setCurrentUser,
    loadRoomAndMessages,
    connectWebSocket,
    sendMessage,
    disconnectWebSocket,
    socket,
    messages,
    roomId, // 추가로 필요한 컴포넌트에서 쓸 수 있게 반환
  };
});
