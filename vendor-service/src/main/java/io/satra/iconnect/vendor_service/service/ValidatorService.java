package io.satra.iconnect.vendor_service.service;

import io.satra.iconnect.vendor_service.dto.ValidatorDTO;
import io.satra.iconnect.vendor_service.exception.InvalidNameException;
import io.satra.iconnect.vendor_service.exception.ValidatorNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface ValidatorService {

    /**
     * Obtains a list of all existing validators
     *
     * @return a set of {@link ValidatorDTO}
     */
    Set<ValidatorDTO> getAllValidators();

    /**
     * Obtains a single validator with given id
     *
     * @param id the id of the validator to be obtained
     * @return a {@link ValidatorDTO}
     * @throws ValidatorNotFoundException if no validator with given id is found
     */
    ValidatorDTO getValidatorById(String id) throws ValidatorNotFoundException;

    /**
     * Creates a new validator
     *
     * @param validatorDTO the validator to be created
     * @return the newly created {@link ValidatorDTO}
     * @throws InvalidNameException if name is null or blank
     */
    ValidatorDTO createValidator(ValidatorDTO validatorDTO) throws InvalidNameException;

    /**
     * Updates an existing validator
     *
     * @param id           the id of the validator to be updated
     * @param validatorDTO the updated validator data
     * @return the updated {@link ValidatorDTO}
     * @throws ValidatorNotFoundException if no validator with given id is found
     * @throws InvalidNameException       if name is null or blank
     */
    ValidatorDTO updateValidator(String id, ValidatorDTO validatorDTO) throws ValidatorNotFoundException, InvalidNameException;

    /**
     * Deletes a validator
     *
     * @param id the id of the validator to be deleted
     * @throws ValidatorNotFoundException if no validator with given id is found
     */
    void deleteValidatorById(String id) throws ValidatorNotFoundException;
}
