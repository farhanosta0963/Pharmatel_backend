package com.pharmatel.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@RestController
@Slf4j
public class HealthController {

    @GetMapping("/healthz")
    public Map<String, String> health() {
        log.debug("Incoming health check request");
        return Map.of("status", "ok");
    }
}
