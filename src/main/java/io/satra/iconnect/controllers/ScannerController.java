package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.scandto.ScanDTO;
import io.satra.iconnect.service.scanner.ScannerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/scanners")
@Slf4j
public class ScannerController {

    @Autowired
    private ScannerService scannerService;

    @PostMapping(value = "/scan")
    @Operation(summary = "Scan a QR code")
    public ResponseEntity<?> scan(@Valid @RequestBody ScanDTO scanDTO) {
        log.debug("API ---> (/api/v1/scanners/scan) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".scan()");
        log.debug("Request body: {}", scanDTO);
        return ResponseEntity.ok(scannerService.scan(scanDTO));
    }
}
