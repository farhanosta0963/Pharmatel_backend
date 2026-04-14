package com.pharmatel.backend.dto.patient;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PatientDto {
    Integer id;
    String name;
    String email;
    String phoneNumber;
}
