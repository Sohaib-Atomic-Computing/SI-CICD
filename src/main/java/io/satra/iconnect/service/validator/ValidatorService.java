package io.satra.iconnect.service.validator;

import io.satra.iconnect.dto.ValidatorDTO;
import io.satra.iconnect.dto.request.ValidatorLoginRequestDTO;
import io.satra.iconnect.dto.request.ValidatorRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.entity.Validator;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ValidatorService {

    /**
     * This method login the vendor using the vendor validator.
     *
     * @param validatorLoginRequestDTO the validator information to log in the vendor
     * @return {@link JwtResponseDTO} with the logged in validator
     * @throws BadRequestException if the validator is not found
     */
    JwtResponseDTO loginValidator(ValidatorLoginRequestDTO validatorLoginRequestDTO) throws BadRequestException;

    /**
     * This method is used to add a new validator
     *
     * @param validatorRequestDTO the validator information to register
     * @return the created validator {@link ValidatorDTO}
     * @throws BadRequestException if the validator already exists
     */
    ValidatorDTO createValidator(ValidatorRequestDTO validatorRequestDTO) throws BadRequestException;

    /**
     * This method is used to update a validator
     *
     * @param id the id of the validator to be updated
     * @param validatorRequestDTO the validator information to update
     * @return the updated validator {@link ValidatorDTO}
     * @throws BadRequestException if the validator already exists
     */
    ValidatorDTO updateValidator(String id, ValidatorRequestDTO validatorRequestDTO) throws BadRequestException;

    /**
     * This method is used to delete a validator
     *
     * @param id the id of the validator to be deleted
     * @throws EntityNotFoundException if the validator does not exist
     */
    void deleteValidator(String id) throws EntityNotFoundException;

    /**
     * This method is used to get a validator by given id
     *
     * @param id the id of the validator to be obtained
     * @return the validator data {@link ValidatorDTO}
     * @throws EntityNotFoundException if the validator does not exist
     */
    ValidatorDTO getValidator(String id) throws EntityNotFoundException;

    /**
     * This method is used to get the validator entity by given id
     *
     * @param id the id of the validator to be obtained
     * @return validator the validator entity {@Link Validator}
     * @throws EntityNotFoundException if the validator does not exist
     */
    Validator getValidatorEntityById(String id) throws EntityNotFoundException;

    /**
     * This method is used to get the validator entity by given name
     *
     * @param name the name of the validator to be obtained
     * @return validator the validator entity {@Link Validator}
     * @throws EntityNotFoundException if the validator does not exist
     */
    Validator getValidatorEntityByName(String name) throws EntityNotFoundException;

    /**
     * This method used to get all validators by vendor id
     *
     * @param vendorId the vendor id of the validators to be obtained
     * @return the validators data {@link ValidatorDTO}
     * @throws EntityNotFoundException if the validators does not exist
     */
    List<ValidatorDTO> getValidatorsByVendorId(String vendorId) throws EntityNotFoundException;

    /**
     * This method is used to get validator by validator key
     *
     * @param key the key of the validator to be obtained
     * @return the validator data {@link ValidatorDTO}
     * @throws EntityNotFoundException if the validator does not exist
     */
    ValidatorDTO getValidatorByKey(String key) throws EntityNotFoundException;
}
