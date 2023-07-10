package io.satra.iconnect.controllers;

import io.satra.iconnect.dto.PromotionDTO;
import io.satra.iconnect.dto.scandto.ScannerMessageDTO;
import io.satra.iconnect.dto.request.PromotionRequestDTO;
import io.satra.iconnect.dto.scandto.ScanDTO;
import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.promotion.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MERCHANT')")
    @Operation(
            summary = "Create a promotion",
            description = "This endpoint creates a new promotion.\nYou have to have the role 'ROLE_ADMIN' to access this endpoint. " +
                    "To create a promotion, you have to provide the following information:\n" +
                    "- name: the name of the promotion\n" +
                    "- description: the description of the promotion\n" +
                    "- isActive: the status of the promotion *default value is true*\n" +
                    "- startDate: the start date of the promotion\n" +
                    "- endDate: the end date of the promotion\n" +
                    "- vendorId: the id of the vendor that created the promotion\n" +
                    "- userIds: the ids of the users that are eligible for the promotion\n"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Promotion created successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{\"message\": \"Promotion created successfully\",\"success\": true,\"data\": {\"id\": \"5f9f1b9b0b2b4b0001b5b1b1\",\"name\": \"Promotion 1\",\"description\": \"Promotion 1\",\"isActive\": true,\"startDate\": \"2020-10-30T00:00:00.000+00:00\",\"endDate\": \"2020-11-30T00:00:00.000+00:00\",\"vendorId\": \"5f9f1b9b0b2b4b0001b5b1b0\",\"userIds\": [\"5f9f1b9b0b2b4b0001b5b1b0\"]}}}"
                                    )
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vendor info or user info didn't found!",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Vendor not found!",
                                                    value = "{\"message\": \"No vendor with given id found!\",\"success\": false,\"data\": null}"
                                            ),
                                            @ExampleObject(
                                                    name = "User not found!",
                                                    value = "{\"message\": \"User not found!\",\"success\": false,\"data\": null}"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Promotion already exists!",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{\"message\": \"Promotion already exists!\",\"success\": false,\"data\": null}"
                                    )
                            )
                    }
            )})
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotionRequestDTO promotionRequestDTO) throws BadRequestException {
        log.debug("API ---> (/api/v1/promotions) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".createPromotion()");
        log.debug("Request body: {}", promotionRequestDTO);
        PromotionDTO promotionDTO = promotionService.createPromotion(promotionRequestDTO);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/promotions/" + promotionDTO.getId()).toUriString());
        ResponseDTO.ResponseDTOBuilder<PromotionDTO> responseDTOBuilder = ResponseDTO.builder();
        return ResponseEntity.created(uri).body(
                        responseDTOBuilder
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MERCHANT')")
    @Operation(summary = "Update a promotion")
    public ResponseEntity<?> updatePromotion(@PathVariable String id, @Valid @RequestBody PromotionRequestDTO promotionRequestDTO)
            throws EntityNotFoundException {
        log.debug("API ---> (/api/v1/promotions/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".updatePromotion()");
        log.debug("Request parameters: id={}", id);
        log.debug("Request body: {}", promotionRequestDTO);
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MERCHANT')")
    @Operation(summary = "Delete a promotion")
    public ResponseEntity<?> deletePromotion(@PathVariable String id) throws EntityNotFoundException {
        log.debug("API ---> (/api/v1/promotions/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".deletePromotion()");
        log.debug("Request parameters: id={}", id);
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
    @Operation(summary = "Get a promotion by id")
    public ResponseEntity<?> getPromotion(@PathVariable String id) throws EntityNotFoundException {
        log.debug("API ---> (/api/v1/promotions/{id}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getPromotion()");
        log.debug("Request parameters: id={}", id);
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
    @Operation(summary = "Get all promotions with pagination and filters")
    public ResponseEntity<?> getAllPromotions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String startDateFrom,
            @RequestParam(required = false) String startDateTo,
            @RequestParam(required = false) String endDateFrom,
            @RequestParam(required = false) String endDateTo,
            Pageable page) {
        log.debug("API ---> (/api/v1/promotions) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getAllPromotions()");
        log.debug("Request parameters: name={}, isActive={}, startDateFrom={}, startDateTo={}, endDateFrom={}, endDateTo={}, page={}",
                name, isActive, startDateFrom, startDateTo, endDateFrom, endDateTo, page);
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
    @Operation(summary = "Get all promotions for a vendor")
    public ResponseEntity<?> getPromotionsByVendor(@PathVariable String vendorId) throws EntityNotFoundException {
        log.debug("API ---> (/api/v1/promotions/vendor/{vendorId}) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getPromotionsByVendor()");
        log.debug("Request parameters: vendorId={}", vendorId);
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
    @Operation(summary = "Validate a QR code and get the promotion associated with it")
    public ResponseEntity<?> getPromotionByQRCode(@Valid @RequestBody ScanDTO scanDTO) throws EntityNotFoundException {
        log.debug("API ---> (/api/v1/promotions/scanner/validate) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".getPromotionByQRCode()");
        log.debug("Request Body: {}", scanDTO);
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
    @Operation(summary = "Encrypt the user id and return the encrypted string")
    public ResponseEntity<?> encryptQRCode(@Valid @RequestBody ScannerMessageDTO scannerMessageDTO) throws BadRequestException {
        log.debug("API ---> (/api/v1/promotions/scanner/encrypt) has been called.");
        log.debug("Method Location: {}", this.getClass().getName() + ".encryptQRCode()");
        log.debug("Request Body: {}", scannerMessageDTO);
        return ResponseEntity.ok(promotionService.encrypt(scannerMessageDTO));
    }
}
