package com.pharmatel.backend.mapper;

import com.pharmatel.backend.dto.pharmacy.PharmacyDto;
import com.pharmatel.backend.dto.pharmacy.PharmacyMedicineDto;
import com.pharmatel.backend.entity.Pharmacy;
import com.pharmatel.backend.entity.PharmacyMedicines;
import org.springframework.stereotype.Component;

@Component
public class PharmacyMapper {

    public PharmacyDto toDto(Pharmacy pharmacy) {
        Double lat = null;
        Double lng = null;
        if (pharmacy.getLocation() != null) {
            lat = pharmacy.getLocation().getY();
            lng = pharmacy.getLocation().getX();
        }
        return PharmacyDto.builder()
            .id(pharmacy.getId())
            .name(pharmacy.getName())
            .lat(lat)
            .lng(lng)
            .pharmacistName(pharmacy.getPharmacistName())
            .build();
    }

    public PharmacyMedicineDto toMedicineDto(PharmacyMedicines pharmacyMedicines) {
        return PharmacyMedicineDto.builder()
            .pharmacyMedicineId(pharmacyMedicines.getId())
            .medicineId(pharmacyMedicines.getMedicine().getId())
            .medicineName(pharmacyMedicines.getMedicine().getName())
            .quantity(pharmacyMedicines.getQuantity())
            .build();
    }
}
