package com.pharmatel.backend.dto.observation;

import lombok.Data;


@Data
public class UpdateObservationRequest {
    private String symptomType;
    private String measurementUnit;
    private Boolean valueBoolean;
    private Double valueNumeric;
    private String valueText;
}
