package com.prithvianilk.mybestofriendo.contextservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CommitmentAction(
        @NotNull
        CommitmentActionType type,

        @NotNull
        @Valid
        Commitment commitment,

        Long id) {
}

