package com.pharmatel.backend.dto.doseschedule;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateDoseScheduleRequest {
    @NotNull(message = "prescriptionId is required")
    private UUID prescriptionId;
    @NotNull(message = "takeAt is required")
    private LocalDateTime takeAt;
    private Boolean taken;
    private LocalDateTime takenAt;
    private String patientPersonalNote;
}
