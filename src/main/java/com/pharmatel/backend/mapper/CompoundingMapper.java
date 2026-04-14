// package com.pharmatel.backend.mapper;

// import com.pharmatel.backend.dto.compounding.CompoundingDto;
// import com.pharmatel.backend.entity.Compounding;
// import org.springframework.stereotype.Component;

// @Component
// public class CompoundingMapper {
//     public CompoundingDto toDto(Compounding compounding) {
//         return CompoundingDto.builder()
//             .id(compounding.getId())
//             .medicineId(compounding.getMedicine() == null ? null : compounding.getMedicine().getId())
//             .medicineName(compounding.getMedicine() == null ? null : compounding.getMedicine().getName())
//             .chemicalId(compounding.getChemical() == null ? null : compounding.getChemical().getId())
//             .chemicalName(compounding.getChemical() == null ? null : compounding.getChemical().getFormalName())
//             .quantity(compounding.getQuantity())
//             .build();
//     }
// }
