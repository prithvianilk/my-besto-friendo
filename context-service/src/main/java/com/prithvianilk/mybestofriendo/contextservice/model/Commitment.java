package com.prithvianilk.mybestofriendo.contextservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record Commitment(
        @NotNull Instant committedAt,

        @NotBlank String description,

        Instant toBeCompletedAt) {

    /**
     * This solves for an LLM having complete context in IST timestamp but returns
     * an instant value
     * We convert the value back to an IST instant, such that underlying
     */
    public Commitment {
        if (toBeCompletedAt != null) {
            toBeCompletedAt = toBeCompletedAt
                    .minus(5, ChronoUnit.HOURS)
                    .minus(30, ChronoUnit.MINUTES);
        }
    }
}
