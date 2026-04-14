package com.pharmatel.backend.dto.medicine;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class MedicineDto {
    Integer id;
    String name;
    String buyPrice;
    String sellPrice;
    String pharmaceuticalForm;
    String box;
    String capacity;
    String capacityMetric;
    String factory;
    Boolean byPharmacist; 
    UUID accountId;
    String drugComposition;
     
}
