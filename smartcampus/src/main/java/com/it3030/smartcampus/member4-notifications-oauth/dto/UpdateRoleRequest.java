package com.it3030.smartcampus.member4.dto;

import com.it3030.smartcampus.member4.model.Role;

import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull Role role) {
}