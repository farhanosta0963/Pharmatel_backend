package com.pharmatel.backend.dto.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePatientRequest {
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;
    @NotNull(message = "accountId is required")
    private UUID accountId;
}
