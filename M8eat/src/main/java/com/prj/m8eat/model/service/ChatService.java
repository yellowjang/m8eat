package com.prj.m8eat.model.service;

import java.util.List;

import com.prj.m8eat.model.dto.ChatMessage;
import com.prj.m8eat.model.dto.ChatRoom;

public interface ChatService {

	ChatRoom getOrCreateRoom(String userA, String userB);

	List<ChatMessage> getMessages(Long roomId);

	void saveMessage(ChatMessage message);
}
