package com.prithvianilk.mybestofriendo.contextservice.service;

import com.prithvianilk.mybestofriendo.contextservice.mapper.CommitmentMapper;
import com.prithvianilk.mybestofriendo.contextservice.model.Commitment;
import com.prithvianilk.mybestofriendo.contextservice.model.IsACommitment;
import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import com.prithvianilk.mybestofriendo.contextservice.repository.CommitmentRepository;
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
    private final CommitmentRepository commitmentRepository;
    private final CommitmentMapper mapper;

    public CommitmentRecorderWhatsAppMessageService(
            WhatsAppMessageRepository repository,
            CommitmentRepository commitmentRepository,
            ChatClient chatClient,
            CommitmentMapper mapper) {
        super(repository);
        this.chatClient = chatClient;
        this.commitmentRepository = commitmentRepository;
        this.mapper = mapper;
    }

    @Override
    public void onNewWhatsAppMessage(WhatsAppMessage message) {
        String messageHistorySnapshot = buildMessageHistorySnapshot();
        String prompt = buildCommitmentDetectionPrompt(messageHistorySnapshot);
        IsACommitment response = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(IsACommitment.class);

        log.info("Commitment detection response: {}", response);

        if (!response.isCommitment()) {
            return;
        }

        Commitment commitment = response.commitment();

        if (commitmentExists(commitment)) {
            log.info("Commitment already exists, skipping save: {}", commitment);
            return;
        }

        commitmentRepository.save(mapper.toEntity(commitment));
    }

    private boolean commitmentExists(Commitment commitment) {
        return commitmentRepository.existsByToBeCompletedAtAndParticipantAndCommitmentMessageContent(
                commitment.toBeCompletedAt(),
                commitment.participant(),
                commitment.commitmentMessageContent());
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
                
                It could also be a reply to an ask for a commitment.
                For example:
                - "[person 1] Hey, lets meet for sushi tmmrw?"
                - "[person 2] Yup, I'm in.
                
                Here, the second message is a commitment.
                
                Review the conversation and determine if any commitments were made in the latest message.
                If a commitment is found, extract:
                - committedAt: The timestamp when the commitment was made. Expected format: 2025-11-03T17:00:00Z
                - description: A brief description of the commitment.
                - participant: The name of the person who made the commitment
                - toBeCompletedAt:
                  - The timestamp when the user committed to complete the task (e.g., if they say "I'll meet you for dinner at 5pm tomorrow", this would be tomorrow at 5pm with the appropriate date). Expected format: 2025-11-03T17:00:00Z
                  - If a date is not mentioned, but a category of day is mentioned (morning, evening, etc), take morning as 9AM, afternoon as 1PM, evening as 4PM, night as 7PM.
                  - If a date is not mentioned and a category is also not mentioned, take the time as 12PM.
                - commitmentMessageContent: The content of the message where the original commitment was asked for.
                  - e.g., if they say "I'll meet you for dinner at 5pm tomorrow", the message content is "I'll meet you for dinner at 5pm tomorrow"
                  - This is not the reply or acknowledgement message, but rather the original message that started the commitment
                
                Set isCommitment to true if a commitment is found, false otherwise.
                If no commitment is found, the commitment object can be null.
                
                Conversation:
                %s
                """
                .formatted(messageSnapshot);
    }
}
