package com.pharmatel.backend.dto.auth;

import com.pharmatel.backend.security.AppRole;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class AuthResponse {
    String token;
    UUID accountId;
    String username;
    AppRole role;
    Integer patientId;
    Integer pharmacyId;
}
