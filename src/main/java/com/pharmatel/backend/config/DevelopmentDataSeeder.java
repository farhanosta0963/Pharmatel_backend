// package com.pharmatel.backend.config;

// import com.pharmatel.backend.entity.Account;
// // import com.pharmatel.backend.entity.Chemical;
// // import com.pharmatel.backend.entity.Compounding;
// import com.pharmatel.backend.entity.DoseSchedule;
// // import com.pharmatel.backend.entity.Factory;
// import com.pharmatel.backend.entity.Medicine;
// import com.pharmatel.backend.entity.Observation;
// import com.pharmatel.backend.entity.ObservationSession;
// import com.pharmatel.backend.entity.Patient;
// import com.pharmatel.backend.entity.Pharmacy;
// import com.pharmatel.backend.entity.PharmacyMedicines;
// import com.pharmatel.backend.entity.Prescription;
// import com.pharmatel.backend.repository.AccountRepository;
// // import com.pharmatel.backend.repository.ChemicalRepository;
// // import com.pharmatel.backend.repository.CompoundingRepository;
// import com.pharmatel.backend.repository.DoseScheduleRepository;
// // import com.pharmatel.backend.repository.FactoryRepository;
// import com.pharmatel.backend.repository.MedicineRepository;
// import com.pharmatel.backend.repository.ObservationRepository;
// import com.pharmatel.backend.repository.ObservationSessionRepository;
// import com.pharmatel.backend.repository.PatientRepository;
// import com.pharmatel.backend.repository.PharmacyMedicinesRepository;
// import com.pharmatel.backend.repository.PharmacyRepository;
// import com.pharmatel.backend.repository.PrescriptionRepository;
// import lombok.RequiredArgsConstructor;
// import org.locationtech.jts.geom.Coordinate;
// import org.locationtech.jts.geom.GeometryFactory;
// import org.locationtech.jts.geom.Point;
// import org.locationtech.jts.geom.PrecisionModel;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.util.List;
// import java.util.UUID;

// @Component
// @RequiredArgsConstructor
// public class DevelopmentDataSeeder implements CommandLineRunner {

//     private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

//     private final AccountRepository accountRepository;
//     private final PatientRepository patientRepository;
//     private final PharmacyRepository pharmacyRepository;
//     // private final FactoryRepository factoryRepository;
//     private final MedicineRepository medicineRepository;
//     private final PharmacyMedicinesRepository pharmacyMedicinesRepository;
//     private final PrescriptionRepository prescriptionRepository;
//     private final DoseScheduleRepository doseScheduleRepository;
//     private final ObservationSessionRepository observationSessionRepository;
//     private final ObservationRepository observationRepository;
//     private final PasswordEncoder passwordEncoder;

//     @Override
//     @Transactional
//     public void run(String... args) {
//         Account patientAccount1 = getOrCreateAccount("patient:john.doe", "password123");
//         Account patientAccount2 = getOrCreateAccount("patient:maria.khan", "password123");
//         Account pharmacyAccount1 = getOrCreateAccount("pharmacy:citymed", "password123");
//         Account pharmacyAccount2 = getOrCreateAccount("pharmacy:wellcare", "password123");

//         Patient john = getOrCreatePatient(patientAccount1, "John Doe", "john.doe@example.com", "+1-212-555-0100");
//         Patient maria = getOrCreatePatient(patientAccount2, "Maria Khan", "maria.khan@example.com", "+1-212-555-0101");

//         Pharmacy cityMed = getOrCreatePharmacy(pharmacyAccount1, "CityMed Pharmacy", "Ahsan Rahman", -74.0060, 40.7128);
//         Pharmacy wellCare = getOrCreatePharmacy(pharmacyAccount2, "WellCare Pharmacy", "Elena Park", -73.9851, 40.7589);

//         // Factory pfizer = getOrCreateFactory("Pfizer");
//         // Factory novartis = getOrCreateFactory("Novartis");
//         // Factory gsk = getOrCreateFactory("GlaxoSmithKline");

//         Medicine metformin = getOrCreateMedicine("Metformin", "5.20", "8.90", "tablet", 30, 500.0, "mg", "pfizer");
//         Medicine lisinopril = getOrCreateMedicine("Lisinopril", "4.80", "7.50", "tablet", 30, 10.0, "mg", "novartis");
//         Medicine atorvastatin = getOrCreateMedicine("Atorvastatin", "6.40", "10.20", "tablet", 30, 20.0, "mg", "gsk");
//         Medicine paracetamol = getOrCreateMedicine("Paracetamol", "2.10", "4.20", "tablet", 20, 500.0, "mg", "gsk");
//         Medicine amoxicillin = getOrCreateMedicine("Amoxicillin", "3.95", "6.90", "capsule", 21, 500.0, "mg", "pfizer");



//         ensureStock(cityMed, metformin, 180);
//         ensureStock(cityMed, lisinopril, 120);
//         ensureStock(cityMed, paracetamol, 220);
//         ensureStock(wellCare, metformin, 75);
//         ensureStock(wellCare, atorvastatin, 140);
//         ensureStock(wellCare, amoxicillin, 90);

//         Prescription rxJohnMetformin = getOrCreatePrescription(
//             john, metformin, cityMed, "500mg", "Twice daily",
//             LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30), "after_meal"
//         );
//         Prescription rxJohnLisinopril = getOrCreatePrescription(
//             john, lisinopril, cityMed, "10mg", "Once daily",
//             LocalDate.of(2026, 2, 1), LocalDate.of(2026, 8, 1), "any_time"
//         );
//         Prescription rxMariaAtorvastatin = getOrCreatePrescription(
//             maria, atorvastatin, wellCare, "20mg", "Nightly",
//             LocalDate.of(2026, 3, 1), LocalDate.of(2026, 9, 1), "after_meal"
//         );
//         Prescription rxMariaAmoxicillin = getOrCreatePrescription(
//             maria, amoxicillin, wellCare, "500mg", "Every 8 hours",
//             LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 17), "after_meal"
//         );

//         DoseSchedule dsJohnMorning = getOrCreateDoseSchedule(rxJohnMetformin, LocalDateTime.of(2026, 3, 20, 8, 0), true, "Taken after breakfast");
//         getOrCreateDoseSchedule(rxJohnMetformin, LocalDateTime.of(2026, 3, 20, 20, 0), false, null);
//         getOrCreateDoseSchedule(rxJohnLisinopril, LocalDateTime.of(2026, 3, 20, 7, 0), false, null);
//         DoseSchedule dsMariaNight = getOrCreateDoseSchedule(rxMariaAtorvastatin, LocalDateTime.of(2026, 3, 20, 21, 0), false, null);
//         getOrCreateDoseSchedule(rxMariaAmoxicillin, LocalDateTime.of(2026, 3, 20, 6, 0), true, "Dose completed");
//         getOrCreateDoseSchedule(rxMariaAmoxicillin, LocalDateTime.of(2026, 3, 20, 14, 0), false, null);
//         getOrCreateDoseSchedule(rxMariaAmoxicillin, LocalDateTime.of(2026, 3, 20, 22, 0), false, null);

//     }

//     private Point point(double lng, double lat) {
//         Point p = GEOMETRY_FACTORY.createPoint(new Coordinate(lng, lat));
//         p.setSRID(4326);
//         return p;
//     }

//     private Account getOrCreateAccount(String username, String rawPassword) {
//         return accountRepository.findByUsername(username)
//             .orElseGet(() -> accountRepository.save(Account.builder()
//                 .id(UUID.randomUUID())
//                 .username(username)
//                 .password(passwordEncoder.encode(rawPassword))
//                 .build()));
//     }

//     private Patient getOrCreatePatient(Account account, String name, String email, String phone) {
//         return patientRepository.findByAccountId(account.getId())
//             .orElseGet(() -> patientRepository.save(Patient.builder()
//                 .name(name)
//                 .email(email)
//                 .phoneNumber(phone)
//                 .account(account)
//                 .build()));
//     }

//     private Pharmacy getOrCreatePharmacy(Account account, String name, String pharmacist, double lng, double lat) {
//         return pharmacyRepository.findByAccountId(account.getId())
//             .orElseGet(() -> pharmacyRepository.save(Pharmacy.builder()
//                 .name(name)
//                 .pharmacistName(pharmacist)
//                 .location(point(lng, lat))
//                 .account(account)
//                 .build()));
//     }

//     // private Factory getOrCreateFactory(String name) {
//     //     return factoryRepository.findAll().stream()
//     //         .filter(f -> f.getName() != null && f.getName().equalsIgnoreCase(name))
//     //         .findFirst()
//     //         .orElseGet(() -> factoryRepository.save(Factory.builder().name(name).build()));
//     // }

//     private Medicine getOrCreateMedicine(
//         String name,
//         String buyPrice,
//         String sellPrice,
//         String form,
//         Integer box,
//         Double capacity,
//         String capacityMetric,
//         String factory
//     ) {
//         return medicineRepository.findAll().stream()
//             .filter(m -> m.getName() != null && m.getName().equalsIgnoreCase(name))
//             .findFirst()
//             .orElseGet(() -> medicineRepository.save(Medicine.builder()
//                 .name(name)
//                 .buyPrice(new BigDecimal(buyPrice))
//                 .sellPrice(new BigDecimal(sellPrice))
//                 .pharmaceuticalForm(form)
//                 .box(box)
//                 .capacity(capacity)
//                 .capacityMetric(capacityMetric)
//                 .factory(factory)
//                 .build()));
//     }

    

//     private void ensureStock(Pharmacy pharmacy, Medicine medicine, Integer quantity) {
//         boolean exists = pharmacyMedicinesRepository.findAll().stream().anyMatch(pm ->
//             pm.getPharmacy() != null
//                 && pm.getMedicine() != null
//                 && pm.getPharmacy().getId().equals(pharmacy.getId())
//                 && pm.getMedicine().getId().equals(medicine.getId()));
//         if (!exists) {
//             pharmacyMedicinesRepository.save(PharmacyMedicines.builder()
//                 .pharmacy(pharmacy)
//                 .medicine(medicine)
//                 .quantity(quantity)
//                 .build());
//         }
//     }

//     private Prescription getOrCreatePrescription(
//         Patient patient,
//         Medicine medicine,
//         Pharmacy pharmacy,
//         String dose,
//         String frequency,
//         LocalDate startDate,
//         LocalDate endDate,
//         String foodRequirement
//     ) {
//         return prescriptionRepository.findAll().stream()
//             .filter(p -> Boolean.FALSE.equals(p.getDeleted())
//                 && p.getPatient() != null
//                 && p.getMedicine() != null
//                 && p.getPatient().getId().equals(patient.getId())
//                 && p.getMedicine().getId().equals(medicine.getId())
//                 && dose.equalsIgnoreCase(p.getDose())
//                 && startDate.equals(p.getStartDate()))
//             .findFirst()
//             .orElseGet(() -> prescriptionRepository.save(Prescription.builder()
//                 .id(UUID.randomUUID())
//                 .patient(patient)
//                 .medicine(medicine)
//                 .dose(dose)
//                 .frequency(frequency)
//                 .startDate(startDate)
//                 .endDate(endDate)
//                 .issuedAt(LocalDateTime.of(startDate, LocalTime.of(9, 0)))
//                 .byPharmacist(false)
//                 .pharmacy(pharmacy)
//                 .foodRequirement(foodRequirement)
//                 .deleted(false)
//                 .build()));
//     }

  
   
 

    
    
// }
