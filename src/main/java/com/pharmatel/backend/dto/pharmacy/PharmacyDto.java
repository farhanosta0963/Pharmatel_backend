package com.pharmatel.backend.dto.pharmacy;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PharmacyDto {
    Integer id;
    String name;
    String pharmacistName;
}
