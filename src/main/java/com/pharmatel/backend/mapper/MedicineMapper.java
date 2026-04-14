package com.pharmatel.backend.mapper;

import com.pharmatel.backend.dto.medicine.MedicineDto;
import com.pharmatel.backend.entity.Medicine;
import org.springframework.stereotype.Component;

@Component
public class MedicineMapper {

    public MedicineDto toDto(Medicine medicine) {
        return MedicineDto.builder()
            .id(medicine.getId())
            .name(medicine.getName())
            .buyPrice(medicine.getBuyPrice())
            .sellPrice(medicine.getSellPrice())
            .pharmaceuticalForm(medicine.getPharmaceuticalForm())
            .box(medicine.getBox())
            .capacity(medicine.getCapacity())
            .factory(medicine.getFactory() == null ? null : medicine.getFactory())
            .byPharmacist(medicine.getByPharmacist()==null ? null :medicine.getByPharmacist())
            .accountId(medicine.getAccount() == null ? null : medicine.getAccount().getId())
            .drugComposition(medicine.getDrugComposition() == null ? null : medicine.getDrugComposition())
            .build();
    }
}
