package com.prj.m8eat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.prj.m8eat.model.dto.ChatMessage;
import com.prj.m8eat.model.dto.ChatRoom;

@Mapper
public interface ChatDao {

	ChatRoom selectChatRoom(String user1, String user2);

	void insertChatRoom(ChatRoom room);

	List<ChatMessage> selectMessages(Long roomId);

	void insertChatMessage(ChatMessage message);
}
