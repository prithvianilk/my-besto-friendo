package com.prithvianilk.mybestofriendo.contextservice.repository;

import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface CommitmentRepository extends JpaRepository<CommitmentEntity, Long> {

    boolean existsByToBeCompletedAtAndParticipantAndCommitmentMessageContent(
            Instant committedAt,
            String participant,
            String commitmentMessageContent);
}

