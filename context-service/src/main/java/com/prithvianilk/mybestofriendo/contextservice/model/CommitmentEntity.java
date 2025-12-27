package com.prithvianilk.mybestofriendo.contextservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Entity
// TODO: Index on the right fields
@Table(name = "commitment", indexes = {
    @Index(name = "idx_commitment_unique", columnList = "committed_at, participant_number", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommitmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "committed_at")
    private Instant committedAt;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "participant_number")
    private String participantNumber;

    @Column(name = "to_be_completed_at")
    private Instant toBeCompletedAt;

    @Column(name = "calendar_event_id")
    private String calendarEventId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (Objects.isNull(createdAt)) {
            createdAt = Instant.now();
        }
    }
}

