package com.prithvianilk.mybestofriendo.contextservice.controller;

import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentEntity;
import com.prithvianilk.mybestofriendo.contextservice.repository.CommitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/context-service/commitments")
@RequiredArgsConstructor
public class CommitmentController {

    private final Clock clock;

    private final CommitmentRepository commitmentRepository;

    @GetMapping
    public List<CommitmentEntity> getCommitments(@RequestParam(value = "toBeCompletedAfter", required = false) Instant toBeCompletedAfter) {
        if (Objects.isNull(toBeCompletedAfter)) {
            toBeCompletedAfter = Instant.now(clock);
        }

        return commitmentRepository
                .findByToBeCompletedAtAfter(toBeCompletedAfter)
                .stream()
                .toList();
    }
}
