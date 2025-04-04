package com.example.application.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

import java.io.IOException;

@RestController
public class TelemetryDataController {

    @GetMapping("/api/telemetry-data/{telemetryFileName}")
    public ResponseEntity<Resource> getTelemetryData(
            @PathVariable String telemetryFileName
    ) throws IOException {
        Resource resource = new ClassPathResource(telemetryFileName + ".json");
        return ResponseEntity.ok(resource);
    }
}
