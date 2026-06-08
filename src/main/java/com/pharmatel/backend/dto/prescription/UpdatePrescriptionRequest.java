package com.pharmatel.backend.dto.prescription;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdatePrescriptionRequest {
    private String dose;
    private String foodRequirement;

    private String note;
    private Integer timeShift;

}
