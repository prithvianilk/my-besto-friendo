package com.prithvianilk.mybestofriendo.contextservice.service;

import com.prithvianilk.mybestofriendo.contextservice.repository.WhatsAppMessageRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class WhatsAppMessageService {
    protected final WhatsAppMessageRepository repository;

    public abstract void onNewWhatsAppMessage();
}
