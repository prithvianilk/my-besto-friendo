package com.prithvianilk.mybestofriendo.contextservice.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommitmentTest {

    @Test
    void constructorConvertsIstToUtc() {
        Instant committedAt = Instant.parse("2023-10-01T10:00:00Z");
        Instant toBeCompletedAtIst = Instant.parse("2023-10-02T10:00:00Z");
        Instant expectedToBeCompletedAtUtc = Instant.parse("2023-10-02T04:30:00Z");

        Commitment commitment = new Commitment(
                committedAt,
                "Test description",
                toBeCompletedAtIst
        );

        assertEquals(expectedToBeCompletedAtUtc, commitment.toBeCompletedAt());
    }
}
