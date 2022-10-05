package io.satra.iconnect.vendor_service.service;

import io.satra.iconnect.vendor_service.dto.ValidatorDTO;
import io.satra.iconnect.vendor_service.entity.Validator;
import io.satra.iconnect.vendor_service.exception.InvalidNameException;
import io.satra.iconnect.vendor_service.exception.ValidatorNotFoundException;
import io.satra.iconnect.vendor_service.repository.ValidatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidatorServiceImpl implements ValidatorService {

    private final ValidatorRepository validatorRepository;

    @Override
    public Set<ValidatorDTO> getAllValidators() {
        return validatorRepository.findAll().stream().map(Validator::toDTO).collect(Collectors.toSet());
    }

    @Override
    public ValidatorDTO getValidatorById(String id) throws ValidatorNotFoundException {
        Validator validator = validatorRepository.findById(id).orElseThrow(() -> new ValidatorNotFoundException(id));
        return validator.toDTO();
    }

    @Override
    public ValidatorDTO createValidator(ValidatorDTO validatorDTO) throws InvalidNameException {
        if (validatorDTO.getName().isBlank() || validatorDTO.getName() == null) {
            throw new InvalidNameException(validatorDTO.getName());
        }

        Validator validator = validatorDTO.toEntity();
        validator = validatorRepository.save(validator);

        log.info("Created new validator: {}", validator);
        return validator.toDTO();
    }

    @Override
    public ValidatorDTO updateValidator(String id, ValidatorDTO validatorDTO) throws ValidatorNotFoundException, InvalidNameException {
        if (validatorDTO.getName().isBlank() || validatorDTO.getName() == null) {
            throw new InvalidNameException(validatorDTO.getName());
        }

        Validator validator = validatorRepository.findById(id).orElseThrow(() -> new ValidatorNotFoundException(id));
        validator.setName(validatorDTO.getName());
        validator = validatorRepository.save(validator);

        log.info("Updated validator: {}", validator);
        return validator.toDTO();
    }

    @Override
    public void deleteValidatorById(String id) throws ValidatorNotFoundException {
        try {
            validatorRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new ValidatorNotFoundException(id);
        }
    }
}
