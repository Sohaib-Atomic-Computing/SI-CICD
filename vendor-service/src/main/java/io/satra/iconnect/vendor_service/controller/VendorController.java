package io.satra.iconnect.vendor_service.controller;

import io.satra.iconnect.vendor_service.dto.VendorDTO;
import io.satra.iconnect.vendor_service.exception.InvalidNameException;
import io.satra.iconnect.vendor_service.exception.ValidatorNotFoundException;
import io.satra.iconnect.vendor_service.exception.VendorNotFoundException;
import io.satra.iconnect.vendor_service.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vendors")
public class VendorController {

    private final VendorService vendorService;

    /**
     * GET: Obtains a list of all vendors
     *
     * @return a {@link ResponseEntity} with a set of {@link VendorDTO}
     */
    @GetMapping
    public ResponseEntity<Set<VendorDTO>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    /**
     * GET: Obtains a vendor with the given id
     *
     * @param id the id of the vendor to be obtained
     * @return a {@link ResponseEntity} with a a {@link VendorDTO}
     * @throws VendorNotFoundException if no vendor with given id is found
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendorDTO> getVendorById(@PathVariable String id) throws VendorNotFoundException {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    /**
     * POST: Creates a new Vendor
     *
     * @param vendorDTO the vendor to be created
     * @return a {@link ResponseEntity} with the newly created {@link VendorDTO}
     * @throws InvalidNameException if name is null or blank
     */
    @PostMapping
    public ResponseEntity<VendorDTO> createVendor(@RequestBody VendorDTO vendorDTO) throws InvalidNameException {
        VendorDTO createdVendor = vendorService.createVendor(vendorDTO);
        return ResponseEntity.created(URI.create("/api/v1/vendors/" + createdVendor.getId())).body(createdVendor);
    }

    /**
     * PUT: Updates an existing vendor
     *
     * @param id        the id of the vendor to be updated
     * @param vendorDTO the updated vendor data
     * @return a {@link ResponseEntity} with the updated {@link VendorDTO}
     * @throws InvalidNameException    if name is null or blank
     * @throws VendorNotFoundException if no vendor with given id is found
     */
    @PutMapping("/{id}")
    public ResponseEntity<VendorDTO> updateVendor(@PathVariable String id, @RequestBody VendorDTO vendorDTO) throws InvalidNameException, VendorNotFoundException {
        return ResponseEntity.ok(vendorService.updateVendor(id, vendorDTO));
    }

    /**
     * DELETE: Deletes a vendor
     *
     * @param id the id of the vendor to be deleted
     * @return a {@link ResponseEntity}
     * @throws VendorNotFoundException if no vendor with given id is found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable String id) throws VendorNotFoundException {
        vendorService.deleteVendorById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT: Assigns a validator to a vendor
     *
     * @param vendorId    the id of the vendor the validator is to be assigned to
     * @param validatorId the id of the validator to be assigned
     * @return a {@link ResponseEntity} with the updated {@link VendorDTO}
     * @throws VendorNotFoundException    if no vendor with given vendorId is found
     * @throws ValidatorNotFoundException if no validator with given validatorId is found
     */
    @PutMapping("/{vendorId}/validators/{validatorId}")
    public ResponseEntity<VendorDTO> assignValidatorToVendor(@PathVariable String vendorId, @PathVariable String validatorId) throws VendorNotFoundException, ValidatorNotFoundException {
        return ResponseEntity.ok(vendorService.assignValidatorToVendor(vendorId, validatorId));
    }
}
