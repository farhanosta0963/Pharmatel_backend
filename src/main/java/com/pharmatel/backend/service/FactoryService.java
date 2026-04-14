// package com.pharmatel.backend.service;

// import com.pharmatel.backend.dto.PageResponse;
// import com.pharmatel.backend.dto.factory.CreateFactoryRequest;
// import com.pharmatel.backend.dto.factory.FactoryDto;
// import com.pharmatel.backend.dto.factory.UpdateFactoryRequest;
// import com.pharmatel.backend.entity.Factory;
// import com.pharmatel.backend.exception.ForbiddenException;
// import com.pharmatel.backend.exception.ResourceNotFoundException;
// import com.pharmatel.backend.mapper.FactoryMapper;
// import com.pharmatel.backend.repository.FactoryRepository;
// import com.pharmatel.backend.security.AppRole;
// import com.pharmatel.backend.security.AppUserDetails;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class FactoryService {
//     private final FactoryRepository factoryRepository;
//     private final FactoryMapper factoryMapper;

//     public PageResponse<FactoryDto> list(int page, int size) {
//         log.info("List factories page={} size={}", page, size);
//         return PageResponse.from(factoryRepository.findAll(PageRequest.of(page, size)).map(factoryMapper::toDto));
//     }

//     public FactoryDto get(Integer id) {
//         log.info("Get factory id={}", id);
//         return factoryMapper.toDto(factoryRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("Factory not found: " + id)));
//     }

//     @Transactional
//     public FactoryDto create(AppUserDetails user, CreateFactoryRequest request) {
//         log.info("Create factory name={} user={}", request.getName(), user.getUsername());
//         Factory factory = Factory.builder().name(request.getName()).build();
//         return factoryMapper.toDto(factoryRepository.save(factory));
//     }
//     // TODO we may need to add ownership or pharmacy condition 
//     @Transactional
//     public FactoryDto update(AppUserDetails user, Integer id, UpdateFactoryRequest request) {
//         log.info("Update factory id={} user={}", id, user.getUsername());
//         Factory factory = factoryRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("Factory not found: " + id));
//         factory.setName(request.getName());
//         return factoryMapper.toDto(factoryRepository.save(factory));
//     }

//     @Transactional
//     public void delete(AppUserDetails user, Integer id) {
//         log.info("Delete factory id={} user={}", id, user.getUsername());
//         Factory factory = factoryRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("Factory not found: " + id));
//         factoryRepository.delete(factory);
//     }

//     private void ensurePharmacyUser(AppUserDetails user) {
//         if (user == null || user.getRole() != AppRole.PHARMACY) {
//             throw new ForbiddenException("Only pharmacy users can modify factories");
//         }
//     }
// }
