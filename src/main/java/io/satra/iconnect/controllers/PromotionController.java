package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.PromotionDTO;
import io.satra.iconnect.dto.ScannerMessageDTO;
import io.satra.iconnect.dto.request.PromotionRequestDTO;
import io.satra.iconnect.dto.request.ScanDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.promotion.PromotionService;
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
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Slf4j
public class PromotionController {

    private final PromotionService promotionService;

    /**
     * This endpoint creates a new promotion.
     *
     * @param promotionRequestDTO the promotion information to register
     * @return {@link ResponseDTO} with the created promotion {@link PromotionDTO}
     * @throws BadRequestException if the promotion is invalid
     */
    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotionRequestDTO promotionRequestDTO) throws BadRequestException {
        PromotionDTO promotionDTO = promotionService.createPromotion(promotionRequestDTO);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/promotions/" + promotionDTO.getId()).toUriString());
        return ResponseEntity.created(uri).body(
                ResponseDTO.builder()
                        .message("Promotion created successfully")
                        .success(true)
                        .data(promotionDTO)
                        .build()
        );
    }

    /**
     * This endpoint updates the promotion with the given id.
     *
     * @param id the id of the promotion to be updated
     * @param promotionRequestDTO the promotion information to update
     * @return {@link ResponseDTO} with the updated promotion {@link PromotionDTO}
     * @throws EntityNotFoundException if no promotion is found with the given id
     */
    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updatePromotion(@PathVariable String id, @Valid @RequestBody PromotionRequestDTO promotionRequestDTO)
            throws EntityNotFoundException {
        PromotionDTO promotionDTO = promotionService.updatePromotion(id, promotionRequestDTO);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Promotion updated successfully")
                        .success(true)
                        .data(promotionDTO)
                        .build()
        );
    }

    /**
     * This endpoint deletes the promotion with the given id.
     *
     * @param id the id of the promotion to be deleted
     * @return {@link ResponseDTO} confirming the deletion
     * @throws EntityNotFoundException if no promotion is found with the given id
     */
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deletePromotion(@PathVariable String id) throws EntityNotFoundException {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .message("Promotion deleted successfully")
                        .success(true)
                        .build()
        );
    }

    /**
     * This endpoint gets the promotion with the given id.
     *
     * @param id the id of the promotion to be retrieved
     * @return the obtained {@link PromotionDTO}
     * @throws EntityNotFoundException if no promotion is found with the given id
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getPromotion(@PathVariable String id) throws EntityNotFoundException {
        return ResponseEntity.ok(promotionService.getPromotion(id));
    }

    /**
     * This endpoint returns all promotions.
     *
     * @param name the name of the promotion to be retrieved
     * @param isActive the active status of the promotion to be retrieved
     * @param startDateFrom the start date from of the promotion to be retrieved
     * @param startDateTo the start date to of the promotion to be retrieved
     * @param endDateFrom the end date from of the promotion to be retrieved
     * @param endDateTo the end date to of the promotion to be retrieved
     * @param page the current page of the promotion to be obtained
     * @return a {@link Page} of {@link PromotionDTO}
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllPromotions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String startDateFrom,
            @RequestParam(required = false) String startDateTo,
            @RequestParam(required = false) String endDateFrom,
            @RequestParam(required = false) String endDateTo,
            Pageable page) {
        return ResponseEntity.ok(promotionService.getAllPromotions(name, isActive, startDateFrom, startDateTo, endDateFrom,
                endDateTo, page));
    }

    /**
     * This endpoint returns all promotions for the given vendor.
     *
     * @param vendorId the id of the vendor to get the promotions for
     * @return the list of promotions for the given vendor {@link PromotionDTO}
     * @throws EntityNotFoundException if no vendor is found with the given id
     */
    @GetMapping(value = "/vendor/{vendorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getPromotionsByVendor(@PathVariable String vendorId) throws EntityNotFoundException {
        return ResponseEntity.ok(promotionService.getVendorPromotions(vendorId));
    }

    /**
     * This endpoint takes the scanned QR code and returns the promotion associated with it.
     *
     * @param scanDTO the scanned QR code information {@link ScanDTO}
     * @return the promotion associated with the scanned QR code
     * @throws EntityNotFoundException if no promotion is found with the given QR code
     */
    @PostMapping(value = "/scanner/validate")
    @PreAuthorize("hasRole('ROLE_VALIDATOR')")
    public ResponseEntity<?> getPromotionByQRCode(@Valid @RequestBody ScanDTO scanDTO) throws EntityNotFoundException {
        return ResponseEntity.ok(ResponseDTO.builder()
                .message("User has the following promotions")
                .success(true)
                .data(promotionService.promotionScannerValidator(scanDTO))
                .build());
    }

    /**
     * This endpoint takes userId and return the encrypted string that contain json object of the user id.
     *
     * @param scannerMessageDTO the user id information {@link ScannerMessageDTO}
     * @return the encrypted string that contain json object of the user id
     * @throws BadRequestException if the user id is invalid
     */
    @PostMapping(value = "/scanner/encrypt")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> encryptQRCode(@Valid @RequestBody ScannerMessageDTO scannerMessageDTO) throws BadRequestException {
        return ResponseEntity.ok(promotionService.encrypt(scannerMessageDTO));
    }

}
