package com.pharmatel.backend.dto.medicine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateMedicineRequest {
    @NotBlank(message = "name is required")
    private String name;
    private String buyPrice;
    private String sellPrice;
    private String pharmaceuticalForm;
    private String box;
    private String capacity;
    private String capacityMetric;
    private String factory;
    private String drugComposition;
    
}
