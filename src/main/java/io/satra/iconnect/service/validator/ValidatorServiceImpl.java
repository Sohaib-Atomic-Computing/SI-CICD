package io.satra.iconnect.service.validator;

import io.satra.iconnect.dto.ValidatorDTO;
import io.satra.iconnect.dto.request.ValidatorRequestDTO;
import io.satra.iconnect.entity.Validator;
import io.satra.iconnect.entity.Vendor;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.ValidatorRepository;
import io.satra.iconnect.security.UserPrincipal;
import io.satra.iconnect.service.vendor.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidatorServiceImpl implements ValidatorService {

    private final ValidatorRepository validatorRepository;
    private final VendorService vendorService;

    @Override
    public ValidatorDTO createValidator(ValidatorRequestDTO validatorRequestDTO) throws BadRequestException {
        // check if vendor exists
        Vendor vendor = vendorService.findVendorEntityById(validatorRequestDTO.getVendorId());

        // check if validator already exists for the vendor
        if (validatorRepository.findByVendorAndName(vendor, validatorRequestDTO.getName()).isPresent()) {
            throw new BadRequestException("Validator already exists for the vendor");
        }

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // generate license key
        String key = generateKey();

        // create validator entity
        Validator validator = Validator.builder()
                .name(validatorRequestDTO.getName())
                .validatorKey(key)
                .vendor(vendor)
                .createdBy(userPrincipal.getUser())
                .lastModifiedBy(userPrincipal.getUser())
                .build();

        // save validator entity
        validator = validatorRepository.save(validator);

        return validator.toDTO();
    }

    @Override
    public ValidatorDTO updateValidator(String id, ValidatorRequestDTO validatorRequestDTO) throws BadRequestException {
        // check if validator exists
        Validator validator = validatorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Validator not found"));

        // check if vendor exists
        Vendor vendor = vendorService.findVendorEntityById(validatorRequestDTO.getVendorId());

        // check if validator already exists for the vendor
        if (validatorRepository.findByVendorAndName(vendor, validatorRequestDTO.getName()).isPresent()) {
            throw new BadRequestException("Validator already exists for the vendor");
        }

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validator.setName(validatorRequestDTO.getName());
        validator.setLastModifiedBy(userPrincipal.getUser());

        // save validator entity
        validator = validatorRepository.save(validator);

        return validator.toDTO();
    }

    @Override
    public void deleteValidator(String id) throws EntityNotFoundException {
        try {
            validatorRepository.deleteById(id);
        } catch (Exception e) {
            throw new EntityNotFoundException("Validator not found");
        }
    }

    @Override
    public ValidatorDTO getValidator(String id) throws EntityNotFoundException {
        return validatorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Validator not found")).toDTO();
    }

    @Override
    public List<ValidatorDTO> getValidatorsByVendorId(String vendorId) throws EntityNotFoundException {
        // check if vendor exists
        Vendor vendor = vendorService.findVendorEntityById(vendorId);
        return validatorRepository.findAllByVendor(vendor).stream().map(Validator::toDTO).toList();
    }

    @Override
    public ValidatorDTO getValidatorByKey(String key) throws EntityNotFoundException {
        return validatorRepository.findByValidatorKey(key).orElseThrow(() -> new EntityNotFoundException("Validator not found")).toDTO();
    }

    private String generateKey() {
        // generate license key using UUID
        return UUID.randomUUID().toString().replace("-", "");
    }
}
