package com.it3030.smartcampus.member3_ticketing.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
    @NotBlank String title,
    @NotBlank String description,
    @NotNull String category,
    @NotNull String priority,
    String resourceLocation,
    String contactEmail,
    String contactPhone,
    @Size(max = 3, message = "Maximum 3 attachments allowed") List<AttachmentData> attachments
) {
    public record AttachmentData(String dataUrl, String fileName) {}
}
