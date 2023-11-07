package com.gltech.myai.service;

import com.gltech.myai.model.Message;
import com.gltech.myai.model.MessageType;
import com.gltech.myai.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> findAllMessages() {
        List<Message> messages = messageRepository.findAll();
        messages.forEach(message -> System.out.println("Loaded message: " + message.getContent()));
        return messages;
    }

    public static Message create(String content, MessageType type) {
        Message newMessage = new Message();
        newMessage.setContent(content);
        newMessage.setType(type);
        newMessage.setTimestamp(LocalDateTime.now());
        return newMessage;
    }

    public Message saveMessage(String content, MessageType type) {
        return messageRepository.save(create(content, type));
    }

    public void deleteAllMessages() {
        messageRepository.deleteAllInBatch();
    }
}
