package com.prithvianilk.mybestofriendo.contextservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class WhatsAppMessageConfig {
    @Value("${whatsapp.message.max-window-size}")
    private int maxWindowSize;
}
