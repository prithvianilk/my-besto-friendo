package com.prithvianilk.mybestofriendo.contextservice.service;

import com.prithvianilk.mybestofriendo.contextservice.model.IsACommitment;
import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import com.prithvianilk.mybestofriendo.contextservice.repository.WhatsAppMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommitmentRecorderWhatsAppMessageService extends WhatsAppMessageService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ChatClient chatClient;

    public CommitmentRecorderWhatsAppMessageService(WhatsAppMessageRepository repository, ChatClient chatClient) {
        super(repository);
        this.chatClient = chatClient;
    }

    @Override
    public void onNewWhatsAppMessage() {
        String messageHistorySnapshot = buildMessageHistorySnapshot();
        String prompt = buildCommitmentDetectionPrompt(messageHistorySnapshot);
        IsACommitment response = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(IsACommitment.class);
        log.info("Commitment detection response: {}", response);
    }

    private String buildMessageHistorySnapshot() {
        return repository.getMessages().stream()
                .map(this::formatMessage)
                .collect(Collectors.joining("\n"));
    }

    private String formatMessage(WhatsAppMessage message) {
        String formattedTime = message.sentAt()
                .atZone(ZoneId.systemDefault())
                .format(FORMATTER);

        return String.format("[%s] %s: %s", formattedTime, message.senderName(), message.content());
    }

    private String buildCommitmentDetectionPrompt(String messageSnapshot) {
        return """
                Analyze the following conversation to identify commitments made by the user.
                
                A commitment is a statement where the user explicitly or implicitly promises to:
                - Perform a specific action in the future
                - Deliver something by a certain time
                - Meet someone or attend an event
                - Complete a task or responsibility
                
                Examples of commitments:
                - "I'll send you the report tomorrow"
                - "I can help you with that"
                - "Let me get back to you on this"
                - "I'll be there at 5pm"
                
                Review the conversation and determine if any commitments were made.
                If a commitment is found, extract:
                - committedAt: The timestamp when the commitment was made
                - description: A brief description of the commitment
                - participant: The name of the person who made the commitment
                - toBeCompletedAt: The timestamp when the user committed to complete the task (e.g., if they say "I'll meet you for dinner at 5pm tomorrow", this would be tomorrow at 5pm with the appropriate date)
                
                Set isCommitment to true if a commitment is found, false otherwise.
                If no commitment is found, the commitment object can be null.
                
                Conversation:
                %s
                """.formatted(messageSnapshot);
    }
}
