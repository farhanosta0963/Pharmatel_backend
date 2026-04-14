package com.pharmatel.backend.dto.doseschedule;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateDoseScheduleRequest {
    private LocalDateTime takeAt;
    private Boolean taken;
    private LocalDateTime takenAt;
    private String patientPersonalNote;
}
