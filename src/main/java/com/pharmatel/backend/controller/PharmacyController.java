package com.pharmatel.backend.controller;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.pharmacymedicine.CreatePharmacyMedicineRequest;
import com.pharmatel.backend.dto.pharmacymedicine.UpdatePharmacyMedicineRequest;
import com.pharmatel.backend.dto.pharmacy.CreatePharmacyRequest;
import com.pharmatel.backend.dto.pharmacy.PharmacyDto;
import com.pharmatel.backend.dto.pharmacy.PharmacyMedicineDto;
import com.pharmatel.backend.dto.pharmacy.UpdatePharmacyRequest;
import com.pharmatel.backend.security.AppUserDetails;
import com.pharmatel.backend.service.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pharmacies")
@Slf4j
@Tag(name = "Pharmacies", description = "Pharmacy and pharmacy inventory endpoints")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @GetMapping
    @Operation(summary = "List pharmacies", description = "Returns paginated pharmacy list.")
    public PageResponse<PharmacyDto> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list pharmacies page={} size={}", page, size);
        return pharmacyService.findAll(page, size);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby pharmacies", description = "Returns pharmacies ordered by distance from coordinates.")
    public List<PharmacyDto> nearby(@RequestParam double lat, @RequestParam double lng) {
        log.info("Incoming nearby pharmacies lat={} lng={}", lat, lng);
        return pharmacyService.findNearby(lat, lng);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pharmacy by ID", description = "Returns a single pharmacy by identifier.")
    public PharmacyDto get(@PathVariable Integer id) {
        log.info("Incoming get pharmacy id={}", id);
        return pharmacyService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create pharmacy", description = "Creates a new pharmacy.")
    public PharmacyDto create(
        @AuthenticationPrincipal AppUserDetails user,
        @Valid @RequestBody CreatePharmacyRequest request
    ) {
        log.info("Incoming create pharmacy name={}", request.getName());
        return pharmacyService.create(user, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pharmacy", description = "Updates pharmacy fields by ID.")
    public PharmacyDto update(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable Integer id,
        @Valid @RequestBody UpdatePharmacyRequest request
    ) {
        log.info("Incoming update pharmacy id={}", id);
        return pharmacyService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete pharmacy", description = "Deletes pharmacy by ID.")
    public void delete(@AuthenticationPrincipal AppUserDetails user, @PathVariable Integer id) {
        log.info("Incoming delete pharmacy id={}", id);
        pharmacyService.delete(user, id);
    }

    @GetMapping("/{id}/medicines")
    @Operation(summary = "List pharmacy medicines", description = "Returns medicines stocked in a pharmacy.")
    public PageResponse<PharmacyMedicineDto> medicines(@PathVariable Integer id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        log.info("Incoming list pharmacy medicines pharmacyId={}", id);
        return pharmacyService.medicines(id, page, size);
    }

    @GetMapping("/inventory")
    @Operation(summary = "List pharmacy inventory", description = "Returns paginated pharmacy-medicine inventory records.")
    public PageResponse<PharmacyMedicineDto> inventory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list inventory page={} size={}", page, size);
        return pharmacyService.listInventory(page, size);
    }

    @GetMapping("/inventory/{id}")
    @Operation(summary = "Get inventory item by ID", description = "Returns one pharmacy inventory record.")
    public PharmacyMedicineDto inventoryById(@PathVariable Integer id) {
        log.info("Incoming get inventory id={}", id);
        return pharmacyService.getInventoryById(id);
    }

    @GetMapping("/myinventory")
    @Operation(summary = "Get my inventory items", description = "Returns inventory items for the authenticated pharmacy.")
    public PageResponse<PharmacyMedicineDto> myInventory(@AuthenticationPrincipal AppUserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        log.info("Incoming get my inventory user={}", user.getUsername());
        return pharmacyService.listInventoryforMe(user,page,size);
    }

    @PostMapping("/inventory")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create inventory item", description = "Creates a pharmacy medicine inventory record.")
    public PharmacyMedicineDto createInventory(
        @AuthenticationPrincipal AppUserDetails user,
        @Valid @RequestBody CreatePharmacyMedicineRequest request
    ) {
        log.info("Incoming create inventory  by user {} medicineId={}", user.getUsername(), request.getMedicineId());
        return pharmacyService.createInventory(user, request);
    }

    @PutMapping("/inventory/{id}")
    @Operation(summary = "Update inventory item", description = "Updates inventory quantity for a pharmacy medicine record.")
    public PharmacyMedicineDto updateInventory(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable Integer id,
        @Valid @RequestBody UpdatePharmacyMedicineRequest request
    ) {
        log.info("Incoming update inventory id={}", id);
        return pharmacyService.updateInventory(user, id, request);
    }

    @DeleteMapping("/inventory/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete inventory item", description = "Deletes pharmacy medicine inventory record by ID.")
    public void deleteInventory(@AuthenticationPrincipal AppUserDetails user, @PathVariable Integer id) {
        log.info("Incoming delete inventory id={}", id);
        pharmacyService.deleteInventory(user, id);
    }
}
