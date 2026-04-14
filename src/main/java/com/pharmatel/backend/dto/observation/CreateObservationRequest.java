package com.pharmatel.backend.dto.observation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateObservationRequest {

    @NotNull(message = "patientId is required")
    private Integer patientId;


    @NotNull(message = "symptomType is required")
    private String symptomType;

    @NotNull(message = "measurementUnit is required")
    private String measurementUnit;

    private UUID observationSessionId;

    private Boolean valueBoolean;
    private Double valueNumeric;
    private String valueText;
}
