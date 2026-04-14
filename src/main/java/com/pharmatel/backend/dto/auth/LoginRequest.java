package com.pharmatel.backend.dto.auth;

import com.pharmatel.backend.security.AppRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private AppRole role;
}
