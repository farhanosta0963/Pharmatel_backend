package com.pharmatel.backend.dto.prescription;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreatePrescriptionRequest {

    @NotNull(message = "patientId is required")
    private Integer patientId;

    @NotNull(message = "medicineId is required")
    private Integer medicineId;

    private Integer pharmacyId;
    private String dose;
    private String frequency;
    private LocalDateTime startDate;
    private Boolean byPharmacist;
    private LocalDateTime endDate;
    private String foodRequirement;
}
