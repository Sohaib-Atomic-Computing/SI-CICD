package io.satra.iconnect.controllers.application;

import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.entity.ApiKey;
import io.satra.iconnect.service.application.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/api-keys")
@Slf4j
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create an api key for an application")
    public ResponseEntity<?> createApiKey(@RequestParam String applicationId, @RequestParam String name) throws Exception {
        log.debug("API ---> (/api/v1/api-keys) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".createApiKey()");
        log.debug("Request parameters: applicationId={}, name={}", applicationId, name);
        ApiKey apiKey = apiKeyService.createApiKey(applicationId, name);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/api-keys" + apiKey.getId()).toUriString());

        return ResponseEntity.created(uri).body(
                ResponseDTO.builder()
                        .message("API Key created successfully")
                        .success(true)
                        .data(apiKey)
                        .build()
        );
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update an application API Key")
    public ResponseEntity<?> updateApiKey(@PathVariable String id, @RequestParam(required = false) Boolean isActive,
                                          @RequestParam(required = false) String name) throws Exception {
        log.debug("API ---> (/api/v1/api-keys/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".updateApiKey()");
        log.debug("Request parameters: id={}, name={}, isActive={}", id, name, isActive);
        ApiKey apiKey = apiKeyService.updateApiKey(id, isActive, name);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("API Key updated successfully")
                        .success(true)
                        .data(apiKey)
                        .build()
        );
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete API Key")
    public ResponseEntity<?> deleteApiKey(@PathVariable String id) throws Exception {
        log.debug("API ---> (/api/v1/api-keys/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".deleteApiKey()");
        log.debug("Request parameters: id={}", id);
        apiKeyService.deleteApiKey(id);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("API Key deleted successfully")
                        .success(true)
                        .build()
        );
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get API Key by id")
    public ResponseEntity<?> getApiKey(@PathVariable String id) throws Exception {
        log.debug("API --> (/api/v1/api-keys/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getApiKey()");
        log.debug("Request parameters: id={}", id);
        return ResponseEntity.ok(apiKeyService.getApiKey(id));
    }

    @GetMapping(value = "/application/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Application API Keys by application Id")
    public ResponseEntity<?> getApplicationApiKeys(@PathVariable String id) throws Exception {
        log.debug("API ---> (/api/v1/api-keys/application/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getApplicationApiKeys()");
        log.debug("Request parameters: id={}", id);
        return ResponseEntity.ok(apiKeyService.getAllApiKeys(id));
    }
}
