package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.entities.Message;

import java.util.List;

public interface MessageService {
    Message sendMessage(Long matchId, Long senderId, Long receiverId, String content);
    List<Message> getMessages(Long matchId);
}