package com.pharmatel.backend.dto.observation;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class ObservationDto {
    UUID id;
    UUID observationSessionId;
    Integer patientId;
    Integer doseScheduleId;
    String symptomType;
    String measurementUnit;
    Boolean valueBoolean;
    Double valueNumeric;
    String valueText;
    LocalDateTime createdAt;
}
