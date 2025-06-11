package com.prj.m8eat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prj.m8eat.model.dao.ChatDao;
import com.prj.m8eat.model.dto.ChatMessage;
import com.prj.m8eat.model.dto.ChatRoom;

@Service
public class ChatServiceImpl implements ChatService {
	
	private final ChatDao chatDao;
	public ChatServiceImpl(ChatDao chatDao) {
		this.chatDao = chatDao;
	}

	@Override
	public ChatRoom getOrCreateRoom(String userA, String userB) {
        String u1 = userA.compareTo(userB) < 0 ? userA : userB;
        String u2 = userA.compareTo(userB) < 0 ? userB : userA;

        ChatRoom room = chatDao.selectChatRoom(u1, u2);
        if (room == null) {
            room = new ChatRoom();
            room.setUser1(u1);
            room.setUser2(u2);
            chatDao.insertChatRoom(room);
        }
        return room;
	}

	@Override
	public List<ChatMessage> getMessages(Long roomId) {
		return chatDao.selectMessages(roomId);
	}

	@Override
	public void saveMessage(ChatMessage message) {
		chatDao.insertChatMessage(message);
		
	}

}
