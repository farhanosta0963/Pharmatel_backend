package com.pharmatel.backend.dto.auth;

import com.pharmatel.backend.security.AppRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private AppRole role = AppRole.PATIENT;

    private String name;
    private String email;
    private String phoneNumber;
    private String pharmacyName;
    private String pharmacistName;
    private Double lat;
    private Double lng;
}
