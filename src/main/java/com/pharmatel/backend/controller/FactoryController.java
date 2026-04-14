// package com.pharmatel.backend.controller;

// import com.pharmatel.backend.dto.PageResponse;
// // import com.pharmatel.backend.dto.factory.CreateFactoryRequest;
// // import com.pharmatel.backend.dto.factory.FactoryDto;
// // import com.pharmatel.backend.dto.factory.UpdateFactoryRequest;
// import com.pharmatel.backend.security.AppUserDetails;
// import com.pharmatel.backend.service.FactoryService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/factories")
// @Slf4j
// public class FactoryController {
//     private final FactoryService factoryService;

//     @GetMapping
//     public PageResponse<FactoryDto> list(
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "20") int size
//     ) {
//         log.info("Incoming list factories page={} size={}", page, size);
//         return factoryService.list(page, size);
//     }

//     @GetMapping("/{id}")
//     public FactoryDto get(@PathVariable Integer id) {
//         log.info("Incoming get factory id={}", id);
//         return factoryService.get(id);
//     }

//     @PostMapping
//     @ResponseStatus(HttpStatus.CREATED)
//     public FactoryDto create(
//         @AuthenticationPrincipal AppUserDetails user,
//         @Valid @RequestBody CreateFactoryRequest request
//     ) {
//         log.info("Incoming create factory name={}", request.getName());
//         return factoryService.create(user, request);
//     }

//     @PutMapping("/{id}")
//     public FactoryDto update(
//         @AuthenticationPrincipal AppUserDetails user,
//         @PathVariable Integer id,
//         @Valid @RequestBody UpdateFactoryRequest request
//     ) {
//         log.info("Incoming update factory id={}", id);
//         return factoryService.update(user, id, request);
//     }

//     @DeleteMapping("/{id}")
//     @ResponseStatus(HttpStatus.NO_CONTENT)
//     public void delete(@AuthenticationPrincipal AppUserDetails user, @PathVariable Integer id) {
//         log.info("Incoming delete factory id={}", id);
//         factoryService.delete(user, id);
//     }
// }
