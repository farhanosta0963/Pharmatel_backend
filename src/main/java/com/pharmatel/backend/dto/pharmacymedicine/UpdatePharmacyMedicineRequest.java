package com.pharmatel.backend.dto.pharmacymedicine;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePharmacyMedicineRequest {
    @NotNull(message = "quantity is required")
    private Integer quantity;
}
