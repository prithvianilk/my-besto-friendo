package com.prithvianilk.mybestofriendo.contextservice.service;

import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class WhatsAppMessageTestUtil {

    private static final String DEFAULT_MY_NAME = "Bob";
    private static final String DEFAULT_OTHER_PHONE = "1234567890";
    private static final String DEFAULT_OTHER_NAME = "Alice";
    private static final int DEFAULT_TIME_INCREMENT_SECONDS = 60;

    public record MessageContent(String content, boolean fromMe) {
    }

    public static List<WhatsAppMessage> createMessages(Instant baseTime, MessageContent... messages) {
        List<WhatsAppMessage> result = new ArrayList<>();
        Instant currentTime = baseTime;

        for (MessageContent messageContent : messages) {
            String senderName = messageContent.fromMe() ? DEFAULT_MY_NAME : DEFAULT_OTHER_NAME;

            result.add(new WhatsAppMessage(
                    DEFAULT_OTHER_PHONE,
                    senderName,
                    messageContent.fromMe(),
                    messageContent.content(),
                    currentTime
            ));

            currentTime = currentTime.plusSeconds(DEFAULT_TIME_INCREMENT_SECONDS);
        }

        return result;
    }

    public static List<WhatsAppMessage> createMessages(MessageContent... messages) {
        return createMessages(Instant.parse("2025-01-15T10:00:00Z"), messages);
    }
}

