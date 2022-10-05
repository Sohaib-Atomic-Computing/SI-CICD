package io.satra.iconnect.vendor_service.service;

import io.satra.iconnect.vendor_service.dto.VendorDTO;
import io.satra.iconnect.vendor_service.entity.Validator;
import io.satra.iconnect.vendor_service.entity.Vendor;
import io.satra.iconnect.vendor_service.exception.InvalidNameException;
import io.satra.iconnect.vendor_service.exception.NoVendorForValidatorFoundException;
import io.satra.iconnect.vendor_service.exception.ValidatorNotFoundException;
import io.satra.iconnect.vendor_service.exception.VendorNotFoundException;
import io.satra.iconnect.vendor_service.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    private final ValidatorService validatorService;

    @Override
    public Set<VendorDTO> getAllVendors() {
        return vendorRepository.findAll().stream().map(Vendor::toDTO).collect(Collectors.toSet());
    }

    @Override
    public VendorDTO getVendorById(String id) throws VendorNotFoundException {
        Vendor vendor = vendorRepository.findById(id).orElseThrow(() -> new VendorNotFoundException(id));
        return vendor.toDTO();
    }

    @Override
    public VendorDTO createVendor(VendorDTO vendorDTO) throws InvalidNameException {
        if (vendorDTO.getName().isBlank() || vendorDTO.getName() == null) {
            throw new InvalidNameException(vendorDTO.getName());
        }

        Vendor vendor = vendorDTO.toEntity();

        vendor.setValidators(Collections.emptySet());
        vendor = vendorRepository.save(vendor);

        log.info("Created new vendor: {}", vendor);
        return vendor.toDTO();
    }

    @Override
    public VendorDTO updateVendor(String id, VendorDTO vendorDTO) throws VendorNotFoundException, InvalidNameException {
        if (vendorDTO.getName().isBlank() || vendorDTO.getName() == null) {
            throw new InvalidNameException(vendorDTO.getName());
        }

        Vendor vendor = vendorRepository.findById(id).orElseThrow(() -> new VendorNotFoundException(id));
        vendor.setName(vendorDTO.getName());
        vendor = vendorRepository.save(vendor);

        log.info("Updated vendor: {}", vendor);
        return vendor.toDTO();
    }

    @Override
    public void deleteVendorById(String id) throws VendorNotFoundException {
        try {
            vendorRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new VendorNotFoundException(id);
        }
    }

    @Override
    public VendorDTO assignValidatorToVendor(String vendorId, String validatorId) throws VendorNotFoundException, ValidatorNotFoundException {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new VendorNotFoundException(vendorId));
        Validator validator = validatorService.getValidatorById(validatorId).toEntity();

        vendor.getValidators().add(validator);
        vendor = vendorRepository.save(vendor);

        log.info("Validator {} was assigned to vendor {}", validator, vendor);
        return vendor.toDTO();
    }

    @Override
    public VendorDTO getVendorByValidatorId(String validatorId) throws NoVendorForValidatorFoundException, ValidatorNotFoundException {
        Validator validator = validatorService.getValidatorById(validatorId).toEntity();

        Vendor vendor = vendorRepository.findFirstByValidatorsContaining(validator).orElseThrow(() -> new NoVendorForValidatorFoundException(validatorId));
        log.info("Found vendor {} for validator ID {}", vendor, validatorId);

        return vendor.toDTO();
    }
}
