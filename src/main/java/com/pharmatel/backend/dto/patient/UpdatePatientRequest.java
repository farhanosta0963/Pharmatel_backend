package com.pharmatel.backend.dto.patient;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdatePatientRequest {
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    private String phoneNumber;
}
