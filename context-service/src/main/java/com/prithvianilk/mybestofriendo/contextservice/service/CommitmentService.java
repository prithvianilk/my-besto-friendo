package com.prithvianilk.mybestofriendo.contextservice.service;

import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentEntity;
import com.prithvianilk.mybestofriendo.contextservice.repository.CommitmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommitmentService {

    private final CommitmentRepository commitmentRepository;
    private final CalendarEventService calendarEventService;

    @Transactional
    public void deleteByCommitmentId(Long id) {
        log.info("Deleting commitment with ID: {}", id);
        Optional<CommitmentEntity> commitmentOptional = commitmentRepository.findById(id);
        if (commitmentOptional.isEmpty()) {
            log.warn("Commitment with ID: {} not found", id);
            return;
        }

        CommitmentEntity commitment = commitmentOptional.get();
        commitmentRepository.delete(commitment);
        deleteCalendarEvent(commitment.getCalendarEventId());
        log.info("Successfully deleted commitment with ID: {}", id);
    }

    private void deleteCalendarEvent(String calendarEventId) {
        if (Objects.isNull(calendarEventId)) {
            return;
        }

        log.info("Deleting associated calendar event with ID: {}", calendarEventId);
        calendarEventService.deleteEvent(calendarEventId);
        log.info("Successfully deleted associated calendar event with ID: {}", calendarEventId);
    }
}
