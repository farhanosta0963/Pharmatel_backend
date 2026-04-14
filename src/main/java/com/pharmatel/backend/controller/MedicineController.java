package com.pharmatel.backend.controller;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.medicine.CreateMedicineRequest;
import com.pharmatel.backend.dto.medicine.MedicineDto;
import com.pharmatel.backend.dto.medicine.UpdateMedicineRequest;
import com.pharmatel.backend.security.AppUserDetails;
import com.pharmatel.backend.service.MedicineService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/medicines")
@Slf4j
@Tag(name = "Medicines", description = "Medicine management endpoints")
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    @Operation(summary = "List medicines", description = "Returns paginated medicines with optional name filter.")
    public PageResponse<MedicineDto> list(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Incoming list medicines name={} page={} size={}", name, page, size);
        return medicineService.list(name, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID", description = "Returns medicine details by identifier.")
    public MedicineDto get(@PathVariable Integer id) {
        log.info("Incoming get medicine id={}", id);
        return medicineService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create medicine", description = "Creates a medicine record from request DTO.")
    public MedicineDto create(
        @AuthenticationPrincipal AppUserDetails user,
        @Valid @RequestBody CreateMedicineRequest request
    ) {
        log.info("Incoming create medicine name={}", request.getName());
        return medicineService.create(user, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update medicine", description = "Updates an existing medicine by ID.")
    public MedicineDto update(
        @AuthenticationPrincipal AppUserDetails user,
        @PathVariable Integer id,
        @Valid @RequestBody UpdateMedicineRequest request
    ) {
        log.info("Incoming update medicine id={}", id);
        return medicineService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete medicine", description = "Deletes medicine by ID.")
    public void delete(@AuthenticationPrincipal AppUserDetails user, @PathVariable Integer id) {
        log.info("Incoming delete medicine id={}", id);
        medicineService.delete(user, id);
    }
}
