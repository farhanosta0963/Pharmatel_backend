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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.PageRequest;
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

    public PageResponse<PharmacyDto> findAll(int page, int size) {
        log.info("Listing pharmacies page={} size={}", page, size);
        return PageResponse.from(pharmacyRepository.findAll(PageRequest.of(page, size)).map(pharmacyMapper::toDto));
    }

    public List<PharmacyDto> findNearby(double lat, double lng) {
        log.info("Listing nearby pharmacies lat={} lng={}", lat, lng);
        return pharmacyRepository.findNearby(lat, lng).stream().map(pharmacyMapper::toDto).toList();
    }

    public PharmacyDto getById(Integer id) {
        log.info("Get pharmacy id={}", id);
        return pharmacyMapper.toDto(fetch(id));
    }

    @Transactional
    public PharmacyDto create(AppUserDetails user, CreatePharmacyRequest request) {
        ensurePharmacyUser(user);
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
        ensurePharmacyUser(user);
        log.info("Updating pharmacy id={} by user={}", id, user.getUsername());
        Pharmacy pharmacy = fetch(id);
        pharmacy.setName(request.getName());
        pharmacy.setPharmacistName(request.getPharmacistName());
        pharmacy.setLocation(point(request.getLng(), request.getLat()));
        return pharmacyMapper.toDto(pharmacyRepository.save(pharmacy));
    }

    @Transactional
    public void delete(AppUserDetails user, Integer id) {
        ensurePharmacyUser(user);
        log.info("Deleting pharmacy id={} by user={}", id, user.getUsername());
        pharmacyRepository.delete(fetch(id));
    }

    public List<PharmacyMedicineDto> medicines(Integer pharmacyId) {
        log.info("List pharmacy medicines pharmacyId={}", pharmacyId);
        Pharmacy pharmacy = fetch(pharmacyId);

        return pharmacyMedicinesRepository.findByPharmacyId(pharmacy.getId()).stream()
            .map(pharmacyMapper::toMedicineDto)
            .toList();
    }
    // TODO make inventory return only for the pharmacy that asked not all pharmacies, 
    
    public PageResponse<PharmacyMedicineDto> listInventory(int page, int size) {
        log.info("List pharmacy inventory page={} size={}", page, size);
        return PageResponse.from(pharmacyMedicinesRepository.findAll(PageRequest.of(page, size)).map(pharmacyMapper::toMedicineDto));
    }

    public PageResponse<PharmacyMedicineDto> listInventoryforMe(AppUserDetails user, int page, int size) {
        log.info("List pharmacy inventory page={} size={}", page, size);
        return PageResponse.from(pharmacyMedicinesRepository.findAll(PageRequest.of(page, size)).map(pharmacyMapper::toMedicineDto));
    }

    public PharmacyMedicineDto getInventoryById(Integer id) {
        log.info("Get pharmacy inventory id={}", id);
        PharmacyMedicines pm = pharmacyMedicinesRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pharmacy medicine not found: " + id));
        return pharmacyMapper.toMedicineDto(pm);
    }

    @Transactional
    public PharmacyMedicineDto createInventory(AppUserDetails user, CreatePharmacyMedicineRequest request) {
        ensurePharmacyUser(user);
        log.info("Create pharmacy inventory pharmacyId={} medicineId={} by user={}", request.getPharmacyId(), request.getMedicineId(), user.getUsername());
        Pharmacy pharmacy = fetch(request.getPharmacyId());
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
            .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + request.getMedicineId()));

        PharmacyMedicines pm = pharmacyMedicinesRepository.findByPharmacyIdAndMedicineId(pharmacy.getId(), medicine.getId())
            .orElse(PharmacyMedicines.builder().pharmacy(pharmacy).medicine(medicine).build());
        pm.setQuantity(request.getQuantity());
        return pharmacyMapper.toMedicineDto(pharmacyMedicinesRepository.save(pm));
    }

    @Transactional // TODO kinda redundant with createInventory, can be merged into one method with some checks
    public PharmacyMedicineDto updateInventory(AppUserDetails user, Integer id, UpdatePharmacyMedicineRequest request) {
        ensurePharmacyUser(user);
        log.info("Update pharmacy inventory id={} by user={}", id, user.getUsername());
        PharmacyMedicines pm = pharmacyMedicinesRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pharmacy medicine not found: " + id));
        pm.setQuantity(request.getQuantity());
        return pharmacyMapper.toMedicineDto(pharmacyMedicinesRepository.save(pm));
    }

    @Transactional
    public void deleteInventory(AppUserDetails user, Integer id) {
        ensurePharmacyUser(user);
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
}
