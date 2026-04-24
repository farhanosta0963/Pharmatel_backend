package com.pharmatel.backend.dto.pharmacymedicine;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePharmacyMedicineRequest {
    @NotNull(message = "medicineId is required")
    private Integer medicineId;
    @NotNull(message = "quantity is required")
    private Integer quantity;
}
