package com.prithvianilk.mybestofriendo.contextservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record Commitment(
        @NotNull
        Instant committedAt,
        
        @NotBlank
        String description,
        
        Instant toBeCompletedAt) {
}
