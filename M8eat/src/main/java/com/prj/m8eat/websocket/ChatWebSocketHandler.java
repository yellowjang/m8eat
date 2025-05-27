package com.prj.m8eat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj.m8eat.model.dto.ChatMessage;
import com.prj.m8eat.model.service.ChatService;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // roomId -> List of sessions
    private final Map<Long, List<WebSocketSession>> roomSessions = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ChatService chatService;

    public ChatWebSocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 연결되면 아무것도 안함, 메시지 오면 처리
    	System.out.println("✅ WebSocket 연결됨: " + session.getId());
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        
        System.out.println("payload " + payload);
        String type = (String) payload.get("type");

        if ("join".equals(type)) {
            Long roomId = Long.valueOf(payload.get("roomId").toString());

            roomSessions.computeIfAbsent(roomId, k -> new ArrayList<>());
            List<WebSocketSession> sessions = roomSessions.get(roomId);
            if (!sessions.contains(session)) {
                sessions.add(session);
            }

            System.out.println("👥 방 입장: roomId=" + roomId + ", session=" + session.getId());
            return;
        }

        // 채팅 메시지 전송 로직
        ChatMessage chatMessage = objectMapper.convertValue(payload, ChatMessage.class);
        chatService.saveMessage(chatMessage);

        List<WebSocketSession> sessions = roomSessions.get(chatMessage.getRoomId());
        if (sessions != null) {
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                }
            }
        }
    }


//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        // 메시지를 JSON -> 객체로 변환
//        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
//
//        // 메시지 저장
//        chatService.saveMessage(chatMessage);
//
//        // 현재 방에 접속한 세션 저장
//        roomSessions.computeIfAbsent(chatMessage.getRoomId(), k -> new ArrayList<>());
//        List<WebSocketSession> sessions = roomSessions.get(chatMessage.getRoomId());
//
//        if (!sessions.contains(session)) {
//            sessions.add(session);
//        }
//
//        // 방에 있는 사람들한테 메시지 전송
//        for (WebSocketSession s : sessions) {
//            if (s.isOpen()) {
//                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
//            }
//        }
//    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 모든 방에서 세션 제거
        for (List<WebSocketSession> sessions : roomSessions.values()) {
            sessions.remove(session);
        }
    }
    
    
}
