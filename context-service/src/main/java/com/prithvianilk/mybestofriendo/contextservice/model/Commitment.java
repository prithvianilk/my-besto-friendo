package com.prithvianilk.mybestofriendo.contextservice.model;

import java.time.Instant;

public record Commitment(
        Instant committedAt,
        String description,
        String commitmentMessageContent,
        String participant,
        Instant toBeCompletedAt) {
}
