package com.prithvianilk.mybestofriendo.contextservice.repository;

import com.prithvianilk.mybestofriendo.contextservice.config.WhatsAppMessageConfig;
import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

@Repository
@RequiredArgsConstructor
public class WhatsAppMessageRepository {
    private final Queue<WhatsAppMessage> messages = new LinkedList<>();
    private final WhatsAppMessageConfig config;

    public void add(WhatsAppMessage message) {
        messages.offer(message);
        removeOldestMessagesIfExceedingMaxWindowSize();
    }

    private void removeOldestMessagesIfExceedingMaxWindowSize() {
        while (messages.size() > config.getMaxWindowSize()) {
            messages.poll();
        }
    }

    public Collection<WhatsAppMessage> getMessages() {
        return messages;
    }
}
