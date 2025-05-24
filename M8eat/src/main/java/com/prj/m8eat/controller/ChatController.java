package com.prj.m8eat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prj.m8eat.model.dto.ChatMessage;
import com.prj.m8eat.model.dto.ChatRoom;
import com.prj.m8eat.model.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	
	private final ChatService chatService;
	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}
	
    // 채팅방 조회 또는 생성
    @GetMapping("/room")
    public ResponseEntity<ChatRoom> getOrCreateRoom(
            @RequestParam String user1,
            @RequestParam String user2) {
    	System.out.println("getOrCreateRoom " + user1 + " " + user2);
        ChatRoom room = chatService.getOrCreateRoom(user1, user2);
        return ResponseEntity.ok(room);
    }
	
    // 채팅 메시지 목록 조회
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long roomId) {
        List<ChatMessage> messages = chatService.getMessages(roomId);
        if (messages == null || messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(messages);
    }

    // 채팅 메시지 전송
    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessage message) {
        chatService.saveMessage(message);
        return ResponseEntity.ok(message);
    }
}
