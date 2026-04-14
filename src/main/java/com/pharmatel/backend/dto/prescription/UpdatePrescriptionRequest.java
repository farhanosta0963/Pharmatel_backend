package com.pharmatel.backend.dto.prescription;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdatePrescriptionRequest {
    private String dose;
    private String frequency;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean byPharmacist;
    private String foodRequirement;
    private Integer pharmacyId;
}
