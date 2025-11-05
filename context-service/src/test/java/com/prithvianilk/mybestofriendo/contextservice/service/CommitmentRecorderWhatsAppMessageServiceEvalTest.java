package com.prithvianilk.mybestofriendo.contextservice.service;

import com.prithvianilk.mybestofriendo.contextservice.model.Commitment;
import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentEntity;
import com.prithvianilk.mybestofriendo.contextservice.model.IsACommitment;
import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import com.prithvianilk.mybestofriendo.contextservice.repository.CommitmentRepository;
import com.prithvianilk.mybestofriendo.contextservice.repository.WhatsAppMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static com.prithvianilk.mybestofriendo.contextservice.service.WhatsAppMessageTestUtil.MessageContent;
import static com.prithvianilk.mybestofriendo.contextservice.service.WhatsAppMessageTestUtil.createMessages;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommitmentRecorderWhatsAppMessageServiceEvalTest {

    @Autowired
    private CommitmentRecorderWhatsAppMessageService service;

    @Autowired
    private WhatsAppMessageRepository repository;

    @Autowired
    private CommitmentRepository commitmentRepository;

    @BeforeEach
    void setUp() {
        repository.getMessages().clear();
        commitmentRepository.deleteAll();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testCases")
    void testCommitmentDetection(
            String testCaseName,
            List<WhatsAppMessage> inputMessages,
            IsACommitment expectedOutput) {
        for (WhatsAppMessage message : inputMessages) {
            repository.add(message);
        }

        service.onNewWhatsAppMessage(inputMessages.getLast());

        assertNotNull(expectedOutput);

        List<CommitmentEntity> actualCommitments = commitmentRepository.findAll();
        int actualCount = actualCommitments.size();
        int expectedCount = expectedOutput.isCommitment() ? 1 : 0;
        assertEquals(expectedCount, actualCount,
                String.format("Expected %d commitment(s) in repository, but found %d", expectedCount, actualCount));

        if (!expectedOutput.isCommitment() || expectedOutput.commitment() == null) {
            return;
        }

        Commitment expectedCommitment = expectedOutput.commitment();
        CommitmentEntity actualCommitmentEntity = actualCommitments.getFirst();

//        assertEquals(expectedCommitment.committedAt(), actualCommitmentEntity.getCommittedAt(),
//                "CommittedAt timestamp mismatch");
        assertEquals(expectedCommitment.commitmentMessageContent(), actualCommitmentEntity.getCommitmentMessageContent(),
                "Commitment message content mismatch");
        assertEquals(expectedCommitment.participant(), actualCommitmentEntity.getParticipant(),
                "Participant mismatch");
//        assertEquals(expectedCommitment.toBeCompletedAt(), actualCommitmentEntity.getToBeCompletedAt(),
//                "ToBeCompletedAt timestamp mismatch");
    }

    static Stream<Arguments> testCases() {
        Instant baseTime = Instant.parse("2025-01-15T10:00:00Z");

        return Stream.of(
                Arguments.of(
                        "Simple commitment - 'I'll send you the report tomorrow'",
                        createMessages(baseTime,
                                new MessageContent("Can you send me the report?", false),
                                new MessageContent("I'll send you the report tomorrow", true)
                        ),
                        new IsACommitment(
                                new Commitment(
                                        baseTime.plusSeconds(60),
                                        "I'll send you the report tomorrow",
                                        "Can you send me the report?",
                                        "Bob",
                                        baseTime.plusSeconds(60).plusSeconds(86400)
                                ),
                                true
                        )
                ),
                Arguments.of(
                        "No commitment - casual conversation",
                        createMessages(baseTime,
                                new MessageContent("How are you?", false),
                                new MessageContent("I'm doing well, thanks!", true)
                        ),
                        new IsACommitment(null, false)
                ),
                Arguments.of(
                        "Commitment with specific time - 'I'll be there at 5pm'",
                        createMessages(baseTime,
                                new MessageContent("Are you coming to the meeting?", false),
                                new MessageContent("Yes, I'll be there at 5pm", true)
                        ),
                        new IsACommitment(
                                new Commitment(
                                        baseTime.plusSeconds(60),
                                        "I'll be there at 5pm",
                                        "Are you coming to the meeting?",
                                        "Bob",
                                        baseTime.plusSeconds(60).plusSeconds(25200)
                                ),
                                true
                        )
                ),
                Arguments.of(
                        "Implicit commitment - 'I can help you with that'",
                        createMessages(baseTime,
                                new MessageContent("Can you help me with this project?", false),
                                new MessageContent("I can help you with that", true)
                        ),
                        new IsACommitment(
                                new Commitment(
                                        baseTime.plusSeconds(60),
                                        "I can help you with that",
                                        "Can you help me with this project?",
                                        "Bob",
                                        null
                                ),
                                true
                        )
                )
        );
    }
}

