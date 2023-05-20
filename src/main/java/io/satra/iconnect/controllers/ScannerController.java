package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.scandto.ScanDTO;
import io.satra.iconnect.entity.ApiKey;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.application.ApiKeyService;
import io.satra.iconnect.service.scanner.ScannerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/scanners")
@Slf4j
public class ScannerController {
    @Autowired
    private ApiKeyService apiKeyService;
    @Autowired
    private ScannerService scannerService;

    @PostMapping(value = "/scan")
    @Operation(summary = "Scan a QR code")
    public ResponseEntity<?> scan(@RequestHeader(name = "x-api-key", required = true) String apiKey,
                                  @Valid @RequestBody ScanDTO scanDTO) throws Exception {
        log.info("API ---> (/api/v1/scanners/scan) has been called.");
        log.info("Method Location: {}", this.getClass().getName() + ".scan()");
        log.info("Request body: {}", scanDTO);

        apiKeyService.validateApiKey(apiKey);

        return ResponseEntity.ok(scannerService.scan(scanDTO));
    }
}
