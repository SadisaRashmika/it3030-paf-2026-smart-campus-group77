package com.it3030.smartcampus.member3_ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(
    @NotBlank String content
) {}
