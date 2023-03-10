package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.vendor.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
@Slf4j
public class VendorController {

    private final VendorService vendorService;

    /**
     * This endpoint creates a new vendor.
     *
     * @param name the vendor name
     * @param logo the vendor logo
     * @return {@link ResponseDTO} with the created vendor {@link VendorDTO}
     */
    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new vendor")
    public ResponseEntity<?> createVendor(@RequestParam(name = "name") String name,
                                          @RequestParam(name = "logo", required = false) MultipartFile logo)
            throws BadRequestException, IOException {
        log.debug("API ---> (/api/v1/vendors) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".createVendor()");
        log.debug("Request parameters: name={}", name);
        if (logo != null) {
            log.debug("Logo size: {}", logo.getSize());
            log.debug("Logo picture type: {}", logo.getContentType());
        }
        VendorDTO vendorDTO = vendorService.createVendor(name, logo);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/vendors/" + vendorDTO.getId()).toUriString());
        return ResponseEntity.created(uri).body(
                ResponseDTO.builder()
                        .message("Vendor created successfully")
                        .success(true)
                        .data(vendorDTO)
                        .build()
        );
    }

    /**
     * This endpoint updates the vendor with the given id.
     *
     * @param id the id of the vendor to be updated
     * @param name the vendor name
     * @param logo the vendor logo
     * @return {@link ResponseDTO} with the updated vendor {@link VendorDTO}
     * @throws EntityNotFoundException if no vendor is found with the given id
     */
    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a vendor")
    public ResponseEntity<?> updateVendor(@PathVariable String id,
                                          @RequestParam(name = "name") String name,
                                          @RequestParam(name = "logo", required = false) MultipartFile logo)
            throws EntityNotFoundException, IOException {
        log.debug("API ---> (/api/v1/vendors/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".updateVendor()");
        log.debug("Request parameters: id={}, name={}", id, name);
        if (logo != null) {
            log.debug("Logo picture size: {} {}", logo.getSize() / 1024 > 1024 ? logo.getSize() / 1024 / 1024 : logo.getSize() / 1024, logo.getSize() / 1024 > 1024 ? "MB" : "KB");
            log.debug("Logo picture type: {}", logo.getContentType());
        }
        VendorDTO vendorDTO = vendorService.updateVendor(id, name, logo);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Vendor updated successfully")
                        .success(true)
                        .data(vendorDTO)
                        .build()
        );
    }

    /**
     * This endpoint deletes the vendor with the given id.
     *
     * @param id the id of the vendor to be deleted
     * @return {@link ResponseDTO} confirming the deletion
     * @throws EntityNotFoundException if no vendor is found with the given id
     */
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a vendor")
    public ResponseEntity<?> deleteVendor(@PathVariable String id)
            throws EntityNotFoundException {
        log.debug("API ---> (/api/v1/vendors/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".deleteVendor()");
        log.debug("Request parameters: id={}", id);
        vendorService.deleteVendor(id);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Vendor deleted successfully")
                        .success(true)
                        .build()
        );
    }

    /**
     * This endpoint returns the vendor with the given id.
     *
     * @param id the id of the vendor to be obtained
     * @return  the obtained {@link VendorDTO}
     * @throws EntityNotFoundException if no vendor is found with the given id
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get a vendor by id")
    public ResponseEntity<?> getVendor(@PathVariable String id) throws EntityNotFoundException {
        log.debug("API ---> (/api/v1/vendors/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getVendor()");
        log.debug("Request parameters: id={}", id);
        return ResponseEntity.ok(vendorService.findVendorById(id));
    }

    /**
     * This endpoint returns all vendors.
     *
     * @param name the name of the vendors to be obtained
     * @param page the current page of the vendors to be obtained
     * @return a {@link Page} of {@link VendorDTO}
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all vendors with pagination and filters")
    public ResponseEntity<Page<VendorDTO>> getAllVendors(
            @RequestParam(required = false) String name,
            Pageable page) {
        log.debug("API ---> (/api/v1/vendors) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getAllVendors()");
        log.debug("Request parameters: name={}, page={}", name, page);
        return ResponseEntity.ok(vendorService.findAllVendors(name, page));
    }
}
