package com.pharmatel.backend.service;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.pharmacymedicine.CreatePharmacyMedicineRequest;
import com.pharmatel.backend.dto.pharmacymedicine.UpdatePharmacyMedicineRequest;
import com.pharmatel.backend.dto.pharmacy.CreatePharmacyRequest;
import com.pharmatel.backend.dto.pharmacy.PharmacyDto;
import com.pharmatel.backend.dto.pharmacy.PharmacyMedicineDto;
import com.pharmatel.backend.dto.pharmacy.UpdatePharmacyRequest;
import com.pharmatel.backend.entity.Medicine;
import com.pharmatel.backend.entity.Pharmacy;
import com.pharmatel.backend.entity.PharmacyMedicines;
import com.pharmatel.backend.exception.ForbiddenException;
import com.pharmatel.backend.exception.ResourceNotFoundException;
import com.pharmatel.backend.mapper.PharmacyMapper;
import com.pharmatel.backend.repository.MedicineRepository;
import com.pharmatel.backend.repository.PharmacyMedicinesRepository;
import com.pharmatel.backend.repository.PharmacyRepository;
import com.pharmatel.backend.security.AppRole;
import com.pharmatel.backend.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.query.Page;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PharmacyService {
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;
    private final PharmacyMedicinesRepository pharmacyMedicinesRepository;
    private final PharmacyMapper pharmacyMapper;
    private final PasswordEncoder passwordEncoder;

    public PageResponse<PharmacyDto> findAll(int page, int size) {
        log.info("Listing pharmacies page={} size={}", page, size);
        return PageResponse.from(pharmacyRepository.findAll(PageRequest.of(page, size)).map(pharmacyMapper::toDto));
    }

    public List<PharmacyDto> findNearby(double lat, double lng) {
        log.info("Listing nearby pharmacies lat={} lng={}", lat, lng);
        return pharmacyRepository.findNearby(lat, lng).stream().map(pharmacyMapper::toDto).toList();
    }

    public List<PharmacyDto> findNearbyformedicine(double lat, double lng, Integer medicineId) {
        log.info("Listing nearby pharmacies lat={} lng={} medicineId={}", lat, lng, medicineId);
        return pharmacyRepository.findNearbyformedicine(lat, lng, medicineId).stream().map(pharmacyMapper::toDto).toList();
    }

    public PharmacyDto getById(Integer id) {
        log.info("Get pharmacy id={}", id);
        return pharmacyMapper.toDto(fetch(id));
    }

    @Transactional
    public PharmacyDto create(AppUserDetails user, CreatePharmacyRequest request) {
        // ensurePharmacyUser(user);
        log.info("Creating pharmacy name={} by user={}", request.getName(), user.getUsername());
        Pharmacy pharmacy = Pharmacy.builder()
            .name(request.getName())
            .pharmacistName(request.getPharmacistName())
            .location(point(request.getLng(), request.getLat()))
            .build();
        return pharmacyMapper.toDto(pharmacyRepository.save(pharmacy));
    }

    @Transactional
    public PharmacyDto update(AppUserDetails user, Integer id, UpdatePharmacyRequest request) {
        // ensurePharmacyUser(user);
        log.info("Updating pharmacy id={} by user={}", id, user.getUsername());
        Pharmacy pharmacy = fetch(id);
        if(request.getPassword() != null && !request.getPassword().isEmpty()) {
            pharmacy.getAccount().setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if(request.getName() != null && !request.getName().isEmpty()) {
            pharmacy.setName(request.getName());
        }
        if(request.getPharmacistName() != null && !request.getPharmacistName().isEmpty()) {
            pharmacy.setPharmacistName(request.getPharmacistName());
        }
        if(request.getLat() != null && request.getLng() != null) {
            pharmacy.setLocation(point(request.getLng(), request.getLat()));
        }
        return pharmacyMapper.toDto(pharmacyRepository.save(pharmacy));
    }

    @Transactional
    public void delete(AppUserDetails user, Integer id) {
        // ensurePharmacyUser(user);
        log.info("Deleting pharmacy id={} by user={}", id, user.getUsername());
        pharmacyRepository.delete(fetch(id));
    }

    public PageResponse<PharmacyMedicineDto> medicines(Integer pharmacyId, String medicineName, int page, int size) {
        log.info("List pharmacy medicines pharmacyId={}", pharmacyId);
        if (medicineName == null || medicineName.isEmpty()) {
            return PageResponse.from(pharmacyMedicinesRepository.findByPharmacyId(pharmacyId, PageRequest.of(page, size)).map(pharmacyMapper::toMedicineDto));
        }
        return PageResponse.from(pharmacyMedicinesRepository.findByPharmacyIdAndMedicine_NameContainingIgnoreCase(pharmacyId, medicineName, PageRequest.of(page, size)).map(pharmacyMapper::toMedicineDto));
    
    }
    
    public PageResponse<PharmacyMedicineDto> listInventory(int page, int size) {
        log.info("List pharmacy inventory page={} size={}", page, size);
        return PageResponse.from(pharmacyMedicinesRepository.findAll(PageRequest.of(page, size)).map(pharmacyMapper::toMedicineDto));
    }

    public PageResponse<PharmacyMedicineDto> listInventoryforMe(AppUserDetails user, int page, int size) {
        log.info("List pharmacy inventory page={} size={}", page, size);
        Pharmacy pharmacy = pharmacyRepository.findByAccountId(user.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pharmacy account not found: " + user.getAccountId()));


        return PageResponse.from(pharmacyMedicinesRepository.findByPharmacyId(pharmacy.getId(), PageRequest.of(page, size)).map(pharmacyMapper::toMedicineDto));
    }

    public PharmacyMedicineDto getInventoryById(Integer id) {
        log.info("Get pharmacy inventory id={}", id);
        PharmacyMedicines pm = pharmacyMedicinesRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pharmacy medicine not found: " + id));
        return pharmacyMapper.toMedicineDto(pm);
    }

   

    @Transactional
    public PharmacyMedicineDto createInventory(AppUserDetails user, CreatePharmacyMedicineRequest request) {
        // ensurePharmacyUser(user);
        log.info("Create pharmacy inventory medicineId={} by user={}", request.getMedicineId(), user.getUsername());

        Pharmacy pharmacy = pharmacyRepository.findByAccountId(user.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Pharmacy account not found: " + user.getAccountId()));
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
            .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + request.getMedicineId()));

        PharmacyMedicines pm = pharmacyMedicinesRepository.findByPharmacyIdAndMedicineId(pharmacy.getId(), medicine.getId())
            .orElse(PharmacyMedicines.builder().pharmacy(pharmacy).medicine(medicine).build());
        pm.setQuantity(request.getQuantity());
        pm.setPrice(request.getPrice());
        if(request.getQuantity() == null || request.getQuantity() == 0 ) {
            pm.setAvailable(false);
        }else {
        pm.setAvailable(request.getAvailable());}

        return pharmacyMapper.toMedicineDto(pharmacyMedicinesRepository.save(pm));
    }

    @Transactional // TODO kinda redundant with createInventory, can be merged into one method with some checks
    public PharmacyMedicineDto updateInventory(AppUserDetails user, Integer id, UpdatePharmacyMedicineRequest request) {
        // ensurePharmacyUser(user);
        log.info("Update pharmacy inventory id={} by user={}", id, user.getUsername());
        PharmacyMedicines pm = pharmacyMedicinesRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pharmacy medicine not found: " + id));
        pm.setQuantity(request.getQuantity());
        pm.setPrice(request.getPrice());
        if(request.getQuantity() == null || request.getQuantity() == 0 ) {
            pm.setAvailable(false);
        }else {
        pm.setAvailable(request.getAvailable());}

        return pharmacyMapper.toMedicineDto(pharmacyMedicinesRepository.save(pm));
    }

    @Transactional
    public void deleteInventory(AppUserDetails user, Integer id) {
        // ensurePharmacyUser(user);
        log.info("Delete pharmacy inventory id={} by user={}", id, user.getUsername());
        PharmacyMedicines pm = pharmacyMedicinesRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pharmacy medicine not found: " + id));
        pharmacyMedicinesRepository.delete(pm);
    }

    private Pharmacy fetch(Integer id) {
        return pharmacyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pharmacy not found: " + id));
    }

    private Point point(Double lng, Double lat) {
        if (lng == null || lat == null) {
            return null;
        }
        Point p = GEOMETRY_FACTORY.createPoint(new Coordinate(lng, lat));
        p.setSRID(4326);
        return p;
    }

    private void ensurePharmacyUser(AppUserDetails user) {
        if (user == null || user.getRole() != AppRole.PHARMACY) {
            throw new ForbiddenException("Only pharmacy users can modify pharmacy resources");
        }
    }

    public Double findAveragePriceByMedicineId(Integer medicineId) {
        return pharmacyMedicinesRepository.findAveragePriceByMedicineId(medicineId);
    }

    
}
