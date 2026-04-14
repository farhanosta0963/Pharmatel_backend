package com.pharmatel.backend.dto.doseschedule;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class DoseScheduleDto {
    Integer id;
    UUID prescriptionId;
    LocalDateTime takeAt;
    Boolean taken;
    LocalDateTime takenAt;
    String patientPersonalNote;
    LocalDateTime createdAt;
}
