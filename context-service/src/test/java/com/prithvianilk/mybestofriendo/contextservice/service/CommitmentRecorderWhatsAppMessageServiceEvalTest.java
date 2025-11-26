package com.prithvianilk.mybestofriendo.contextservice.service;

import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentEntity;
import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import com.prithvianilk.mybestofriendo.contextservice.repository.CommitmentRepository;
import com.prithvianilk.mybestofriendo.contextservice.repository.WhatsAppMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

import static com.prithvianilk.mybestofriendo.contextservice.service.WhatsAppMessageTestUtil.MessageContent;
import static com.prithvianilk.mybestofriendo.contextservice.service.WhatsAppMessageTestUtil.createMessages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class CommitmentRecorderWhatsAppMessageServiceEvalTest {

    @Autowired
    private CommitmentRecorderWhatsAppMessageService service;

    @Autowired
    private WhatsAppMessageRepository repository;

    @Autowired
    private CommitmentRepository commitmentRepository;

    @MockitoBean
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository.clear();
        commitmentRepository.deleteAll();

        Instant baseTime = Instant.parse("2025-01-15T00:00:00Z");
        when(clock.instant()).thenReturn(baseTime);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testCases")
    void testCreateCommitment(
            String testCaseName,
            List<WhatsAppMessage> inputMessages) {
        for (WhatsAppMessage message : inputMessages) {
            repository.add(message);
        }

        service.onNewWhatsAppMessage(inputMessages.getLast());

        List<CommitmentEntity> actualCommitments = commitmentRepository.findAll();
        assertEquals(
                1,
                actualCommitments.size(),
                "Expected exactly 1 commitment to be created");

        CommitmentEntity actualCommitment = actualCommitments.getFirst();
        assertEquals("1234567890", actualCommitment.getParticipantNumber());
    }

    static Stream<Arguments> testCases() {
        Instant baseTime = Instant.parse("2025-01-15T10:00:00Z");

        return Stream.of(
                Arguments.of(
                        "Simple commitment - 'I'll send you the report tomorrow'",
                        createMessages(baseTime,
                                new MessageContent("Can you send me the report?", false),
                                new MessageContent("I'll send you the report tomorrow", true)
                        )
                ),
                Arguments.of(
                        "Commitment with specific time - 'I'll be there at 5pm'",
                        createMessages(baseTime,
                                new MessageContent("Are you coming to the meeting?", false),
                                new MessageContent("Yes, I'll be there at 5pm", true)
                        )
                ),
                Arguments.of(
                        "Implicit commitment - 'I can help you with that'",
                        createMessages(baseTime,
                                new MessageContent("Can you help me with this project?", false),
                                new MessageContent("I can help you with that", true)
                        )
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("updateTestCases")
    void testUpdateCommitment(
            String testCaseName,
            List<WhatsAppMessage> inputMessages) {
        for (WhatsAppMessage message : inputMessages) {
            repository.add(message);
            service.onNewWhatsAppMessage(message);
        }

        List<CommitmentEntity> actualCommitments = commitmentRepository.findAll();
        assertEquals(1, actualCommitments.size(),
                "Expected exactly 1 commitment in repository after update");

        CommitmentEntity actualCommitment = actualCommitments.getFirst();
        assertEquals("1234567890", actualCommitment.getParticipantNumber());
        // TODO: Assert the updated time as well
    }

    static Stream<Arguments> updateTestCases() {
        Instant baseTime = Instant.parse("2025-01-15T10:00:00Z");

        return Stream.of(
                Arguments.of(
                        "Update meeting time from 5pm to 6pm",
                        createMessages(baseTime,
                                new MessageContent("Hey, let's meet at 5pm?", false),
                                new MessageContent("Yes, sure!", true),
                                new MessageContent("No, let's postpone to 6pm?", false),
                                new MessageContent("sure that works", true)
                        )
                ),
                Arguments.of(
                        "Update party date from 15th to 14th",
                        createMessages(baseTime,
                                new MessageContent("Yo, planning a house party on 15th, you down?", false),
                                new MessageContent("Yessir!", true),
                                new MessageContent("Bro, it's moved to 14th, dat fine?", false),
                                new MessageContent("Yeah no worries.", true)
                        )
                )
        );
    }
}

