package com.prithvianilk.mybestofriendo.contextservice.service;

import com.prithvianilk.mybestofriendo.contextservice.mapper.CommitmentMapper;
import com.prithvianilk.mybestofriendo.contextservice.model.Commitment;
import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentAction;
import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentEntity;
import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import com.prithvianilk.mybestofriendo.contextservice.repository.CommitmentRepository;
import com.prithvianilk.mybestofriendo.contextservice.repository.WhatsAppMessageRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommitmentRecorderWhatsAppMessageService extends WhatsAppMessageService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ChatClient chatClient;
    private final CommitmentRepository commitmentRepository;
    private final CommitmentMapper mapper;
    private final Validator validator;
    private final Clock clock;

    public CommitmentRecorderWhatsAppMessageService(
            WhatsAppMessageRepository repository,
            CommitmentRepository commitmentRepository,
            ChatClient chatClient,
            CommitmentMapper mapper,
            Validator validator,
            Clock clock) {
        super(repository);
        this.chatClient = chatClient;
        this.commitmentRepository = commitmentRepository;
        this.mapper = mapper;
        this.validator = validator;
        this.clock = clock;
    }

    @Override
    public void onNewWhatsAppMessage(WhatsAppMessage message) {
        String messageHistorySnapshot = buildMessageHistorySnapshot(message.participantMobileNumber());
        String futureCommitmentsSnapshot = buildFutureCommitmentsSnapshot();
        String prompt = buildCommitmentDetectionPrompt(messageHistorySnapshot, futureCommitmentsSnapshot);

        log.debug("Prompt:\n{}", prompt);

        CommitmentAction response = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(CommitmentAction.class);

        log.info("Commitment action response: {}", response);

        if (Objects.isNull(response) || !isResponseValid(response)) {
            return;
        }

        Commitment commitment = response.commitment();

        switch (response.type()) {
            case CREATE -> {
                commitmentRepository.save(mapper.toEntity(commitment));
                log.info("Created new commitment: {}", commitment);
            }
            case CHANGE -> {
                if (Objects.isNull(response.id())) {
                    log.warn("Cannot change commitment - ID is required for CHANGE action: {}", commitment);
                    return;
                }
                commitmentRepository.findById(response.id()).ifPresentOrElse(
                        existingCommitment -> {
                            updateCommitmentEntity(existingCommitment, commitment);
                            commitmentRepository.save(existingCommitment);
                            log.info("Updated commitment: {}", commitment);
                        },
                        () -> log.warn("Cannot change commitment - not found with ID: {}", response.id())
                );
            }
            case CANCEL -> {
                if (Objects.isNull(response.id())) {
                    log.warn("Cannot cancel commitment - ID is required for CANCEL action: {}", commitment);
                    return;
                }
                commitmentRepository.findById(response.id()).ifPresentOrElse(
                        existingCommitment -> {
                            commitmentRepository.delete(existingCommitment);
                            log.info("Cancelled commitment: {}", commitment);
                        },
                        () -> log.warn("Cannot cancel commitment - not found with ID: {}", response.id())
                );
            }
        }
    }

    private boolean isResponseValid(CommitmentAction response) {
        Set<ConstraintViolation<CommitmentAction>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            log.warn("Commitment action validation failed: {}", violations);
            return false;
        }
        return true;
    }

    private String buildFutureCommitmentsSnapshot() {
        Instant now = Instant.now(clock);
        return commitmentRepository
                .findAll()
                .stream()
                .filter(entity -> Objects.nonNull(entity.getToBeCompletedAt()) &&
                        entity.getToBeCompletedAt().isAfter(now))
                .map(entity -> String.format("ID:%d|Participant:%s|Description:%s|ToBeCompletedAt:%s",
                        entity.getId(),
                        entity.getParticipant(),
                        entity.getDescription(),
                        entity.getToBeCompletedAt()))
                .collect(Collectors.joining(" || "));
    }

    private void updateCommitmentEntity(CommitmentEntity entity, Commitment commitment) {
        entity.setCommittedAt(commitment.committedAt());
        entity.setDescription(commitment.description());
        entity.setToBeCompletedAt(commitment.toBeCompletedAt());
    }

    private String buildMessageHistorySnapshot(String participantMobileNumber) {
        return repository.getMessages(participantMobileNumber).stream()
                .map(this::formatMessage)
                .collect(Collectors.joining("\n"));
    }

    private String formatMessage(WhatsAppMessage message) {
        String formattedTime = message.sentAt()
                .atZone(ZoneId.systemDefault())
                .format(FORMATTER);

        return String.format("[%s] %s: %s", formattedTime, message.senderName(), message.content());
    }

    private String buildCommitmentDetectionPrompt(String messageSnapshot, String futureCommitmentsSnapshot) {
        return """
                Analyze the following conversation to identify commitments made by the user and determine the appropriate action.
                
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
                
                - "[person 1] Can you send the slides?"
                - "[person 2] Will send them in an hour.
                
                - "[person 1] Are you coming to the party?"
                - "[person 2] Yes, I'll be there.
                
                Here, the second message in each exchange is a commitment.
                IMPORTANT: Whenever the message is replied to in a commiting and positive fashion, assume it's a commitment.
                Even informal responses like yes, yep, ya, etc are commitments.
                
                Review the conversation and determine the action type for the latest message:
                
                Action Types:
                1. CREATE: A new commitment is being made that doesn't modify or cancel an existing one.
                   - IMPORTANT: Before using CREATE, check the "Existing Future Commitments" list below.
                   - If you find a matching commitment in that list (same participant, similar description, or similar message content), DO NOT use CREATE.
                   - Instead, use CHANGE if the commitment is being modified, or CANCEL if it's being withdrawn.
                   - Only use CREATE if the commitment is truly new and not found in the existing commitments list.
                2. CHANGE: An existing commitment is being modified (e.g., changing the time, date, or details).
                   - Examples: "Actually, let's meet at 6pm instead of 5pm", "Can we push that to next week?"
                   - You MUST match this with an existing commitment from the "Existing Future Commitments" list below.
                3. CANCEL: An existing commitment is being cancelled or withdrawn.
                   - Examples: "I can't make it", "Let's cancel that", "Never mind, I won't be able to do that"
                   - You MUST match this with an existing commitment from the "Existing Future Commitments" list below.
                
                Existing Future Commitments:
                The following are existing commitments that are scheduled to be completed in the future.
                - Use these to identify which commitment is being changed or cancelled (for CHANGE/CANCEL actions).
                - Check this list BEFORE using CREATE to ensure you're not creating a duplicate commitment.
                - If a commitment in the conversation matches one in this list, use CHANGE or CANCEL instead of CREATE.
                %s
                
                If a commitment action is found, extract:
                - type: One of CREATE, CHANGE, or CANCEL
                - commitment:
                  - committedAt: The timestamp when the commitment was made. Expected format: 2025-11-03T17:00:00Z
                  - description: A brief description of the commitment. Make this an explicit mention of the commitment task to be done.
                  - participant: The name of the person who made the commitment
                  - toBeCompletedAt:
                    - The timestamp when the user committed to complete the task (e.g., if they say "I'll meet you for dinner at 5pm tomorrow", this would be tomorrow at 5pm with the appropriate date). Expected format: 2025-11-03T17:00:00Z
                    - If a date is not mentioned, but a category of day is mentioned (morning, evening, etc), take morning as 9AM, afternoon as 1PM, evening as 4PM, night as 7PM.
                    - If a date is not mentioned and a category is also not mentioned, take the time as 12PM.
                - id: (REQUIRED for CHANGE and CANCEL actions, null for CREATE)
                  - For CHANGE or CANCEL actions, you MUST identify which existing commitment is being modified or cancelled.
                  - Match the commitment from the conversation with one of the existing future commitments listed above.
                  - Use the ID from the matching commitment in the "Existing Future Commitments" list.
                  - If the action is CREATE, set id to null.
                  - If the action is CHANGE or CANCEL but you cannot find a matching commitment, still set the id to null (but this will cause an error, so try your best to match it).
                
                If no commitment action is found, return null for both type and commitment.
                
                Conversation:
                %s
                """
                .formatted(futureCommitmentsSnapshot, messageSnapshot);
    }
}
