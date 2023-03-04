package io.satra.iconnect.controllers.application;

import io.satra.iconnect.dto.response.ResponseDTO;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.satra.iconnect.entity.Application;
import io.satra.iconnect.service.application.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/applications")
@Slf4j
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create an application")
    public ResponseEntity<?> createApplication(@RequestParam String name) throws Exception {
        log.debug("API ---> (/api/v1/applications/) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".createApplication()");
        log.debug("Request parameters: name={}", name);
        Application application  = applicationService.createApplication(name);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/applications/" + application.getId()).toUriString());
        return ResponseEntity.created(uri).body(
                ResponseDTO.builder()
                        .message("Application created successfully")
                        .success(true)
                        .data(application)
                        .build()
        );
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update an application")
    public ResponseEntity<?> updateApplication(@PathVariable String id, @RequestParam(required = false) Boolean isActive,
                                               @RequestParam(required = false) String name) throws Exception {
        log.debug("API ---> (/api/v1/applications/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".updateApplication()");
        log.debug("Request parameters: id={}, name={}, isActive={}", id, name, isActive);
        Application application  = applicationService.updateApplication(id, isActive, name);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Application updated successfully")
                        .success(true)
                        .data(application)
                        .build()
        );
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete an application")
    public ResponseEntity<?> deleteApplication(@PathVariable String id) throws Exception {
        log.debug("API ---> (/api/v1/applications/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".deleteApplication()");
        log.debug("Request parameters: id={}", id);
        applicationService.deleteApplication(id);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Application deleted successfully")
                        .success(true)
                        .build()
        );
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get an application by id")
    public ResponseEntity<?> getApplication(@PathVariable String id) throws Exception {
        log.debug("API ---> (/api/v1/applications/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getApplication()");
        log.debug("Request parameters: id={}", id);
        return ResponseEntity.ok(applicationService.getApplication(id));
    }

    @GetMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all applications with pagination and filters")
    public ResponseEntity<Page<Application>> getApplications(@RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive, Pageable page) {
        log.debug("API ---> (/api/v1/applications) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getApplications()");
        log.debug("Request parameters: name={}, isActive={}", name, isActive);
        return ResponseEntity.ok(applicationService.getAllApplications(name, isActive, page));
    }
}
