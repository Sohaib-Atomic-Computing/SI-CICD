package io.satra.iconnect.service.validator;

import io.satra.iconnect.dto.ValidatorDTO;
import io.satra.iconnect.dto.request.ValidatorLoginRequestDTO;
import io.satra.iconnect.dto.request.ValidatorRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.entity.Validator;
import io.satra.iconnect.entity.Vendor;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.ValidatorRepository;
import io.satra.iconnect.security.JWTUtils;
import io.satra.iconnect.security.UserPrincipal;
import io.satra.iconnect.service.vendor.VendorService;
import io.satra.iconnect.utils.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidatorServiceImpl implements ValidatorService {

    private final ValidatorRepository validatorRepository;
    private final VendorService vendorService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtResponseDTO loginValidator(ValidatorLoginRequestDTO validatorLoginRequestDTO) throws BadRequestException {

        Validator validator = validatorRepository.findFirstByName(validatorLoginRequestDTO.getUserId())
                .orElseThrow(() -> new BadRequestException("Validator does not exist"));

        // Generate JWT token
        String jwt = generateJWTToken(validatorLoginRequestDTO.getUserId(), validatorLoginRequestDTO.getValidatorKey());

        validator.setToken(jwt);
        validatorRepository.save(validator);

        return JwtResponseDTO.builder()
                .validator(validator.toDTO())
                .token(jwt)
                .build();
    }

    @Override
    public ValidatorDTO createValidator(ValidatorRequestDTO validatorRequestDTO) throws BadRequestException {
        // check if vendor exists
        Vendor vendor = vendorService.findVendorEntityById(validatorRequestDTO.getVendorId());

        // check if validator already exists for the vendor
        if (validatorRepository.findByVendorAndName(vendor, validatorRequestDTO.getName()).isPresent()) {
            throw new BadRequestException("Validator already exists for the vendor");
        }

        // check if the validator name is unique
        if (validatorRepository.findFirstByName(validatorRequestDTO.getName()).isPresent()) {
            throw new BadRequestException("Validator name already exists");
        }

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

        // generate license key
        String key = KeyGeneratorUtil.nextKey();

        // create validator entity
        Validator validator = Validator.builder()
                .name(validatorRequestDTO.getName())
                .validatorKey(key)
                .encodedKey(passwordEncoder.encode(key))
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
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

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
    public Validator getValidatorEntityById(String id) throws EntityNotFoundException {
        return validatorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Validator not found"));
    }

    @Override
    public Validator getValidatorEntityByName(String name) throws EntityNotFoundException {
        return validatorRepository.findFirstByName(name).orElseThrow(() -> new EntityNotFoundException("Validator not found"));
    }

    @Override
    public List<ValidatorDTO> getValidatorsByVendorId(String vendorId) throws EntityNotFoundException {
        // check if vendor exists
        Vendor vendor = vendorService.findVendorEntityById(vendorId);
        return validatorRepository.findAllByVendor(vendor).stream().map(Validator::toDTO).collect(Collectors.toList());
    }

    @Override
    public ValidatorDTO getValidatorByKey(String key) throws EntityNotFoundException {
        return validatorRepository.findByValidatorKey(key).orElseThrow(() -> new EntityNotFoundException("Validator not found")).toDTO();
    }

    /**
     * Generate a JWT token for the validator
     *
     * @param customerIdOrName the validator customer id or validator name
     * @param validatorKey the validator key
     * @return the generated JWT token
     */
    private String generateJWTToken(String customerIdOrName, String validatorKey) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(customerIdOrName, validatorKey));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }
}
