package com.prithvianilk.mybestofriendo.contextservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "commitment", indexes = {
    @Index(name = "idx_commitment_unique", columnList = "committed_at, participant, commitment_message_content", unique = true)
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

    @Column(name = "participant")
    private String participant;

    @Column(name = "to_be_completed_at")
    private Instant toBeCompletedAt;

    @Column(name = "commitment_message_content", length = 2000)
    private String commitmentMessageContent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}

