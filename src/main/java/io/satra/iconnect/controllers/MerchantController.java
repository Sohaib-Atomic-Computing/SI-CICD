package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.MerchantDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.entity.Merchant;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.merchant.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/merchants")
@Slf4j
public class MerchantController {
    @Autowired
    private MerchantService merchantService;

    /**
     * This endpoint updates my current authenticated merchant profile.
     *
     * @param merchant the merchant information to be updated
     *                 {@link Merchant}
     * @return the updated merchant {@link Merchant}
     * @throws EntityNotFoundException if the merchant is not found
     * @throws BadRequestException     if the merchant information is not valid
     */
    @PutMapping("/")
    @Operation(summary = "Update merchant profile")
    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    public ResponseEntity<?> updateMerchantProfile(@RequestBody Merchant merchant)
            throws EntityNotFoundException, BadRequestException {
        log.info("API ---> (/api/v1/merchants) has been called.");
        log.info("Method Location: {}", this.getClass().getName() + ".updateMerchantProfile()");
        log.info("Request body: {}", merchant);

        MerchantDTO merchantDTO = merchantService.updateMerchant(merchant);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Merchant updated successfully")
                        .success(true)
                        .data(merchantDTO)
                        .build()
        );
    }

    /**
     * This endpoint updates merchant profile by merchant id.
     *
     * @param id       the id of the merchant to be updated
     * @param merchant the merchant information to be updated
     *                 {@link Merchant}
     * @return the updated merchant {@link Merchant}
     * @throws EntityNotFoundException if the merchant is not found
     * @throws BadRequestException     if the merchant information is not valid
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update merchant profile")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateMerchantProfile(@Valid @RequestBody Merchant merchant, @PathVariable String id)
            throws EntityNotFoundException, BadRequestException {
        log.info("API ---> (/api/v1/merchants) has been called.");
        log.info("Method Location: {}", this.getClass().getName() + ".updateMerchantProfile()");
        log.info("Request body: {}", merchant);

        MerchantDTO merchantDTO = merchantService.updateMerchantById(id, merchant);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Merchant updated successfully")
                        .success(true)
                        .data(merchantDTO)
                        .build()
        );
    }

    @PutMapping("/{id}/change-logo")
    @Operation(summary = "Update merchant profile logo")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MERCHANT')")
    public ResponseEntity<?> updateMerchantProfile(@RequestPart("logo") MultipartFile logo, @PathVariable String id)
            throws EntityNotFoundException, BadRequestException, IOException {
        log.info("API ---> (/api/v1/merchants) has been called.");
        log.info("Method Location: {}", this.getClass().getName() + ".updateMerchantProfile()");

        if (logo == null) {
            throw new BadRequestException("Merchant logo is required");
        }

        log.debug("Merchant logo size: {} {}", logo.getSize() / 1024 > 1024 ? logo.getSize() / 1024 / 1024 : logo.getSize() / 1024, logo.getSize() / 1024 > 1024 ? "MB" : "KB");
        log.debug("Merchant logo type: {}", logo.getContentType());

        MerchantDTO merchantDTO = merchantService.updateMerchantById(id, logo);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Merchant updated successfully")
                        .success(true)
                        .data(merchantDTO)
                        .build()
        );
    }
}
