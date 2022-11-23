package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.dto.request.VendorRequestDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.vendor.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
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
     * @param vendorRequestDTO the vendor information to register
     * @return {@link ResponseDTO} with the created vendor {@link VendorDTO}
     */
    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createVendor(@Valid @RequestBody VendorRequestDTO vendorRequestDTO) {
        VendorDTO vendorDTO = vendorService.createVendor(vendorRequestDTO);
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
     * @param vendorRequestDTO the vendor information to update
     * @return {@link ResponseDTO} with the updated vendor {@link VendorDTO}
     * @throws EntityNotFoundException if no vendor is found with the given id
     */
    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateVendor(@PathVariable String id, @Valid @RequestBody VendorRequestDTO vendorRequestDTO)
            throws EntityNotFoundException {
        VendorDTO vendorDTO = vendorService.updateVendor(id, vendorRequestDTO);
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
    public ResponseEntity<?> deleteVendor(@PathVariable String id)
            throws EntityNotFoundException {
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
    public ResponseEntity<?> getVendor(@PathVariable String id) throws EntityNotFoundException {
        return ResponseEntity.ok(vendorService.findVendorById(id));
    }

    /**
     * This endpoint returns all vendors.
     *
     * @param page the current page of the vendors to be obtained
     * @return a {@link Page} of {@link VendorDTO}
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<VendorDTO>> getAllVendors(Pageable page) {
        return ResponseEntity.ok(vendorService.findAllVendors(page));
    }
}
