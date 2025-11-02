package com.prithvianilk.mybestofriendo.contextservice.model;

import java.time.Instant;

public record WhatsAppMessage(
    String participantMobileNumber,
    String senderName,
    boolean fromMe,
    String content,
    Instant sentAt
) {}
