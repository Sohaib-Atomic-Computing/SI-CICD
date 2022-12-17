package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.ValidatorDTO;
import io.satra.iconnect.dto.request.ValidatorRequestDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.validator.ValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/validators")
@RequiredArgsConstructor
@Slf4j
public class ValidatorController {

    private final ValidatorService validatorService;

    /**
     * This endpoint creates a new validator.
     *
     * @param validatorRequestDTO the validator information to register
     * @return {@link ResponseDTO} with the created validator {@link ValidatorDTO}
     * @throws BadRequestException if the validator already exists
     */
    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new validator")
    public ResponseEntity<?> createValidator(@Valid @RequestBody ValidatorRequestDTO validatorRequestDTO) throws BadRequestException {
        log.info("Creating validator");
        ValidatorDTO validatorDTO = validatorService.createValidator(validatorRequestDTO);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/validators/" + validatorDTO.getId()).toUriString());
        return ResponseEntity.created(uri).body(
                ResponseDTO.builder()
                        .message("Validator created successfully")
                        .success(true)
                        .data(validatorDTO)
                        .build()
        );
    }

    /**
     * This endpoint updates the validator with the given id.
     *
     * @param id the id of the validator to be updated
     * @param validatorRequestDTO the validator information to update
     * @return {@link ResponseDTO} with the updated validator {@link ValidatorDTO}
     * @throws EntityNotFoundException if no validator is found with the given id
     */
    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a validator by id")
    public ResponseEntity<?> updateValidator(@PathVariable String id, @Valid @RequestBody ValidatorRequestDTO validatorRequestDTO)
            throws EntityNotFoundException {
        log.info("Updating validator with id: {}", id);
        ValidatorDTO validatorDTO = validatorService.updateValidator(id, validatorRequestDTO);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Validator updated successfully")
                        .success(true)
                        .data(validatorDTO)
                        .build()
        );
    }

    /**
     * This endpoint deletes the validator with the given id.
     *
     * @param id the id of the validator to be deleted
     * @return {@link ResponseDTO} with the deleted validator {@link ValidatorDTO}
     * @throws EntityNotFoundException if no validator is found with the given id
     */
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a validator by id")
    public ResponseEntity<?> deleteValidator(@PathVariable String id) throws EntityNotFoundException {
        log.info("Deleting validator with id: {}", id);
        validatorService.deleteValidator(id);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Validator deleted successfully")
                        .success(true)
                        .build()
        );
    }

    /**
     * This endpoint returns the validator with the given id.
     *
     * @param id the id of the validator to be returned
     * @return {@link ResponseDTO} with the validator {@link ValidatorDTO}
     * @throws EntityNotFoundException if no validator is found with the given id
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get a validator by id")
    public ResponseEntity<?> getValidator(@PathVariable String id) throws EntityNotFoundException {
        log.info("Getting validator with id: {}", id);
        ValidatorDTO validatorDTO = validatorService.getValidator(id);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Validator retrieved successfully")
                        .success(true)
                        .data(validatorDTO)
                        .build()
        );
    }

    /**
     * This endpoint returns all validators by the given vendor id.
     *
     * @param vendorId the vendor id of the validators to be returned
     * @return the validators associated with the given vendor {@link ValidatorDTO}
     * @throws EntityNotFoundException if no validator is found with the given vendor id
     */
    @GetMapping(value = "/vendor/{vendorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all vendor validators by vendor id")
    public ResponseEntity<?> getValidatorsByVendor(@PathVariable String vendorId) throws EntityNotFoundException {
        log.info("Getting validators by vendor id: {}", vendorId);
        return ResponseEntity.ok(validatorService.getValidatorsByVendorId(vendorId));
    }
}
