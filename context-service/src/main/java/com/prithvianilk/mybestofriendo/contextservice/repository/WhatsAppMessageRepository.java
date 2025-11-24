package com.prithvianilk.mybestofriendo.contextservice.repository;

import com.prithvianilk.mybestofriendo.contextservice.config.WhatsAppMessageConfig;
import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WhatsAppMessageRepository {
    private final Map<String, Queue<WhatsAppMessage>> messagesByParticipant = new ConcurrentHashMap<>();
    private final WhatsAppMessageConfig config;

    public void add(WhatsAppMessage message) {
        log.debug("Adding message: {}", message);
        Queue<WhatsAppMessage> messages = messagesByParticipant.computeIfAbsent(
                message.participantMobileNumber(),
                _ -> new LinkedList<>());
        messages.offer(message);
        removeOldestMessagesIfExceedingMaxWindowSize(messages);
    }

    private void removeOldestMessagesIfExceedingMaxWindowSize(Queue<WhatsAppMessage> messages) {
        while (messages.size() > config.getMaxWindowSize()) {
            messages.poll();
        }
    }

    public Collection<WhatsAppMessage> getMessages(String participantMobileNumber) {
        return messagesByParticipant.getOrDefault(participantMobileNumber, new LinkedList<>());
    }

    public void clear() {
        messagesByParticipant.clear();
    }
}
