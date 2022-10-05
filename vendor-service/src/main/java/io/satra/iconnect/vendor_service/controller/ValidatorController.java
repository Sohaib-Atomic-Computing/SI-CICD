package io.satra.iconnect.vendor_service.controller;

import io.satra.iconnect.vendor_service.dto.ValidatorDTO;
import io.satra.iconnect.vendor_service.exception.InvalidNameException;
import io.satra.iconnect.vendor_service.exception.ValidatorNotFoundException;
import io.satra.iconnect.vendor_service.service.ValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/validators")
public class ValidatorController {

    private final ValidatorService validatorService;

    /**
     * GET: Obtains a list of all validators
     *
     * @return a {@link ResponseEntity} with a set of {@link ValidatorDTO}
     */
    @GetMapping
    public ResponseEntity<Set<ValidatorDTO>> getAllValidators() {
        return ResponseEntity.ok(validatorService.getAllValidators());
    }

    /**
     * GET: Obtains a validator with the given id
     *
     * @param id the id of the validator to be obtained
     * @return a {@link ResponseEntity} with a a {@link ValidatorDTO}
     * @throws ValidatorNotFoundException if no validator with given id is found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ValidatorDTO> getValidatorById(@PathVariable String id) throws ValidatorNotFoundException {
        return ResponseEntity.ok(validatorService.getValidatorById(id));
    }

    /**
     * POST: Creates a new validator
     *
     * @param validatorDTO the validator to be created
     * @return a {@link ResponseEntity} with the newly created {@link ValidatorDTO}
     * @throws InvalidNameException if name is null or blank
     */
    @PostMapping
    public ResponseEntity<ValidatorDTO> createValidator(@RequestBody ValidatorDTO validatorDTO) throws InvalidNameException {
        ValidatorDTO createdValidator = validatorService.createValidator(validatorDTO);
        return ResponseEntity.created(URI.create("/api/v1/validators/" + createdValidator.getId())).body(createdValidator);
    }

    /**
     * PUT: Updates an existing validator
     *
     * @param id           the id of the validator to be updated
     * @param validatorDTO the updated validator data
     * @return a {@link ResponseEntity} with the updated {@link ValidatorDTO}
     * @throws InvalidNameException       if name is null or blank
     * @throws ValidatorNotFoundException if no validator with given id is found
     */
    @PutMapping("/{id}")
    public ResponseEntity<ValidatorDTO> updateValidator(@PathVariable String id, @RequestBody ValidatorDTO validatorDTO) throws InvalidNameException, ValidatorNotFoundException {
        return ResponseEntity.ok(validatorService.updateValidator(id, validatorDTO));
    }

    /**
     * DELETE: Deletes a validator
     *
     * @param id the id of the validator to be deleted
     * @return a {@link ResponseEntity}
     * @throws ValidatorNotFoundException if no validator with given id is found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteValidator(@PathVariable String id) throws ValidatorNotFoundException {
        validatorService.deleteValidatorById(id);
        return ResponseEntity.ok().build();
    }
}
