package com.prithvianilk.mybestofriendo.contextservice.listener;

import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import com.prithvianilk.mybestofriendo.contextservice.repository.WhatsAppMessageRepository;
import com.prithvianilk.mybestofriendo.contextservice.service.WhatsAppMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppMessageListener {

    private final WhatsAppMessageRepository repository;
    private final List<WhatsAppMessageService> services;

    @KafkaListener(topics = "whatsapp-messages", groupId = "context-service-group")
    public void listen(WhatsAppMessage message) {
        log.info("Received WhatsApp message: {}", message);
        repository.add(message);
        for (WhatsAppMessageService service : services) {
            service.onNewWhatsAppMessage(message);
        }
    }
}
