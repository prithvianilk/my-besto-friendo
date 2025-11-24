package com.prithvianilk.mybestofriendo.contextservice.repository;

import com.prithvianilk.mybestofriendo.contextservice.config.WhatsAppMessageConfig;
import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WhatsAppMessageRepositoryTest {

    private WhatsAppMessageRepository repository;
    private WhatsAppMessageConfig config;

    @BeforeEach
    void setUp() {
        config = mock(WhatsAppMessageConfig.class);
        when(config.getMaxWindowSize()).thenReturn(5);
        repository = new WhatsAppMessageRepository(config);
    }

    @Test
    void testAddAndGetMessagesForDifferentParticipants() {
        WhatsAppMessage msg1 = new WhatsAppMessage("9876543210", "User 1", false, "Hello", Instant.now());
        WhatsAppMessage msg2 = new WhatsAppMessage("0123456789", "User 2", false, "Hi", Instant.now());
        WhatsAppMessage msg3 = new WhatsAppMessage("9876543210", "User 1", false, "How are you?", Instant.now());

        repository.add(msg1);
        repository.add(msg2);
        repository.add(msg3);

        Collection<WhatsAppMessage> user1Messages = repository.getMessages("9876543210");
        assertEquals(2, user1Messages.size());
        assertTrue(user1Messages.contains(msg1));
        assertTrue(user1Messages.contains(msg3));

        Collection<WhatsAppMessage> user2Messages = repository.getMessages("0123456789");
        assertEquals(1, user2Messages.size());
        assertTrue(user2Messages.contains(msg2));

        Collection<WhatsAppMessage> user3Messages = repository.getMessages("1122334455");
        assertTrue(user3Messages.isEmpty());
    }

    @Test
    void testWindowSizeIsEnforcedPerParticipant() {
        when(config.getMaxWindowSize()).thenReturn(2);

        WhatsAppMessage msg1 = new WhatsAppMessage("9876543210", "User 1", false, "1", Instant.now());
        WhatsAppMessage msg2 = new WhatsAppMessage("9876543210", "User 1", false, "2", Instant.now());
        WhatsAppMessage msg3 = new WhatsAppMessage("9876543210", "User 1", false, "3", Instant.now());

        repository.add(msg1);
        repository.add(msg2);
        repository.add(msg3);

        Collection<WhatsAppMessage> user1Messages = repository.getMessages("9876543210");
        assertEquals(2, user1Messages.size());
        assertTrue(user1Messages.contains(msg2));
        assertTrue(user1Messages.contains(msg3));
        
        WhatsAppMessage msgUser2 = new WhatsAppMessage("0123456789", "User 2", false, "A", Instant.now());
        repository.add(msgUser2);
        assertEquals(1, repository.getMessages("0123456789").size());
    }
    
    @Test
    void testClear() {
         WhatsAppMessage msg1 = new WhatsAppMessage("9876543210", "User 1", false, "Hello", Instant.now());
         repository.add(msg1);
         
         repository.clear();
         
         assertTrue(repository.getMessages("9876543210").isEmpty());
    }
}
