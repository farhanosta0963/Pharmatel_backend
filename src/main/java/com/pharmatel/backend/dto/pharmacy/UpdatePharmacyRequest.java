package com.pharmatel.backend.dto.pharmacy;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePharmacyRequest {
    @NotBlank(message = "name is required")
    private String name;
    private String pharmacistName;
    private Double lat;
    private Double lng;
}
