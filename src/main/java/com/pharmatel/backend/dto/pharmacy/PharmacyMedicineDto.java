package com.pharmatel.backend.dto.pharmacy;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PharmacyMedicineDto {
    Integer pharmacyMedicineId;
    Integer medicineId;
    String medicineName;
    Integer quantity;
}
