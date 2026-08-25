package com.pharmatel.backend.dto.medicine;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;

@Value
@Builder
public class MedicineDto {
    Integer id;
    String name;
    String pharmaceuticalForm;
    String box;
    String buyPrice;

    String sellPrice;
    String capacity;
    String capacityMetric;
    String factory;
    Boolean byPharmacist; 
    UUID accountId;
    String drugComposition;
     
}
