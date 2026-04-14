package com.pharmatel.backend.service;

import com.pharmatel.backend.dto.PageResponse;
import com.pharmatel.backend.dto.medicine.CreateMedicineRequest;
import com.pharmatel.backend.dto.medicine.MedicineDto;
import com.pharmatel.backend.dto.medicine.UpdateMedicineRequest;
import com.pharmatel.backend.entity.Account;
// import com.pharmatel.backend.entity.Factory;
import com.pharmatel.backend.entity.Medicine;
import com.pharmatel.backend.entity.Patient;
import com.pharmatel.backend.entity.Pharmacy;
import com.pharmatel.backend.exception.ForbiddenException;
import com.pharmatel.backend.exception.ResourceNotFoundException;
import com.pharmatel.backend.mapper.MedicineMapper;
import com.pharmatel.backend.repository.AccountRepository;
// import com.pharmatel.backend.repository.FactoryRepository;
import com.pharmatel.backend.repository.MedicineRepository;
import com.pharmatel.backend.security.AppRole;
import com.pharmatel.backend.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private final MedicineRepository medicineRepository;
    // private final FactoryRepository factoryRepository;
    private final MedicineMapper medicineMapper;
    private final AccountRepository accountRepository;

    public PageResponse<MedicineDto> list(String name, int page, int size) {
        log.info("Listing medicines nameFilter={} page={} size={}", name, page, size);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Medicine> results = (name == null || name.isBlank())
            ? medicineRepository.findAll(pageRequest)
            : medicineRepository.findByNameContainingIgnoreCase(name, pageRequest);

        return PageResponse.from(results.map(medicineMapper::toDto));
    }

    public MedicineDto get(Integer id) {
        log.info("Get medicine id={}", id);
        return medicineMapper.toDto(medicineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + id)));
    }

    @Transactional
    public MedicineDto create(AppUserDetails user, CreateMedicineRequest request) {
        
        log.info("Creating medicine name={} by user={}", request.getName(), user.getUsername());
        

        Account  account = null;
        
            account = accountRepository.findById(user.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + user.getAccountId()));
        
        Boolean byphBoolean = user.getRole() == AppRole.PHARMACY ? true : false;

        Medicine medicine = Medicine.builder()
            .name(request.getName())
            .buyPrice(request.getBuyPrice())
            .sellPrice(request.getSellPrice())
            .pharmaceuticalForm(request.getPharmaceuticalForm())
            .box(request.getBox())
            .capacity(request.getCapacity())
            .factory(request.getFactory())
            .account(account)
            .byPharmacist(byphBoolean)
            .build();
        return medicineMapper.toDto(medicineRepository.save(medicine));
    }

    @Transactional
    public MedicineDto update(AppUserDetails user, Integer id, UpdateMedicineRequest request) {
        
        log.info("Updating medicine id={} by user={}", id, user.getUsername());
        Medicine medicine = medicineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + id));
        
        Account  account = null;
        
            account = accountRepository.findById(user.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + user.getAccountId()));
        Boolean byphBoolean = user.getRole() == AppRole.PHARMACY ? true : false;

        medicine.setName(request.getName());
        medicine.setBuyPrice(request.getBuyPrice());
        medicine.setSellPrice(request.getSellPrice());
        medicine.setPharmaceuticalForm(request.getPharmaceuticalForm());
        medicine.setBox(request.getBox());
        medicine.setCapacity(request.getCapacity());
        medicine.setFactory(request.getFactory());
        medicine.setAccount(account);
        medicine.setByPharmacist(byphBoolean);
        return medicineMapper.toDto(medicineRepository.save(medicine));
    }

    @Transactional
    public void delete(AppUserDetails user, Integer id) {
        log.info("Deleting medicine id={} by user={}", id, user.getUsername());
        Medicine medicine = medicineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + id));
        medicineRepository.delete(medicine);
    }

    private void ensurePharmacyUser(AppUserDetails user) {
        if (user == null || user.getRole() != AppRole.PHARMACY) {
            throw new ForbiddenException("Only pharmacy users can modify medicines");
        }
    }

    
}
