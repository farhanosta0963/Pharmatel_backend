package com.pharmatel.backend.mapper;

import com.pharmatel.backend.dto.prescription.PrescriptionDto;
import com.pharmatel.backend.entity.Prescription;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper {

    public PrescriptionDto toDto(Prescription prescription) {
        return PrescriptionDto.builder()
            .id(prescription.getId())
            .patientId(prescription.getPatient().getId())
            .medicineId(prescription.getMedicine().getId())
            .medicineName(prescription.getMedicine().getName())
            .dose(prescription.getDose())
            .frequency(prescription.getFrequency())
            .startDate(prescription.getStartDate())
            .endDate(prescription.getEndDate())
            .issuedAt(prescription.getIssuedAt())
            .byPharmacist(prescription.getByPharmacist())
            .pharmacyId(prescription.getPharmacy() == null ? null : prescription.getPharmacy().getId())
            .foodRequirement(prescription.getFoodRequirement())
            .build();
    }
}
