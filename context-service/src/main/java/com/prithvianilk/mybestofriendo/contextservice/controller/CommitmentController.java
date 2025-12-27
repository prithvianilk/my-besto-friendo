package com.prithvianilk.mybestofriendo.contextservice.controller;

import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentEntity;
import com.prithvianilk.mybestofriendo.contextservice.repository.CommitmentRepository;
import com.prithvianilk.mybestofriendo.contextservice.service.CommitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    private final CommitmentService commitmentService;

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

    @DeleteMapping("/{id}")
    public void deleteCommitment(@PathVariable Long id) {
        commitmentService.deleteByCommitmentId(id);
    }
}
