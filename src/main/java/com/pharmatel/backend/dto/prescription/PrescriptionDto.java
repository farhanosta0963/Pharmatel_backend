package com.pharmatel.backend.dto.prescription;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class PrescriptionDto {
    UUID id;
    Integer patientId;
    Integer medicineId;
    String medicineName;
    String dose;
    String frequency;
    LocalDateTime startDate;
    LocalDateTime endDate;
    LocalDateTime issuedAt;
    Boolean byPharmacist;
    Integer pharmacyId;
    String foodRequirement;
}
