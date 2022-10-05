package io.satra.iconnect.vendor_service.service;

import io.satra.iconnect.vendor_service.dto.VendorDTO;
import io.satra.iconnect.vendor_service.exception.InvalidNameException;
import io.satra.iconnect.vendor_service.exception.NoVendorForValidatorFoundException;
import io.satra.iconnect.vendor_service.exception.ValidatorNotFoundException;
import io.satra.iconnect.vendor_service.exception.VendorNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface VendorService {

    /**
     * Obtains a list of all existing vendors
     *
     * @return a set of {@link VendorDTO}
     */
    Set<VendorDTO> getAllVendors();

    /**
     * Obtains a single vendor with given id
     *
     * @param id the id of the vendor to be obtained
     * @return a {@link VendorDTO}
     * @throws VendorNotFoundException if no vendor with given id is found
     */
    VendorDTO getVendorById(String id) throws VendorNotFoundException;

    /**
     * Creates a new vendor
     *
     * @param vendorDTO the vendor to be created
     * @return the newly created {@link VendorDTO}
     * @throws InvalidNameException if name is null or blank
     */
    VendorDTO createVendor(VendorDTO vendorDTO) throws InvalidNameException;

    /**
     * Updates an existing vendor
     *
     * @param id        the id of the vendor to be updated
     * @param vendorDTO the updated vendor data
     * @return the updated {@link VendorDTO}
     * @throws VendorNotFoundException if no vendor with given id is found
     * @throws InvalidNameException    if name is null or blank
     */
    VendorDTO updateVendor(String id, VendorDTO vendorDTO) throws VendorNotFoundException, InvalidNameException;

    /**
     * Deletes a vendor
     *
     * @param id the id of the vendor to be deleted
     * @throws VendorNotFoundException if no vendor with given id is found
     */
    void deleteVendorById(String id) throws VendorNotFoundException;

    /**
     * Assign a validator to a vendor
     *
     * @param vendorId    the id of the vendor to which the validator is to be assigned
     * @param validatorId the id of the validator to be assigned
     * @return the updated {@link VendorDTO} with the newly assigned validator
     * @throws VendorNotFoundException    if no vendor with given vendorId is found
     * @throws ValidatorNotFoundException if no validator with given validatorId is found
     */
    VendorDTO assignValidatorToVendor(String vendorId, String validatorId) throws VendorNotFoundException, ValidatorNotFoundException;

    /**
     * Obtains the vendor which is related to the given validator
     *
     * @param validatorId the id of the validator
     * @return the {@link VendorDTO} which is related to the given validator
     * @throws NoVendorForValidatorFoundException if no validator for given validator is found
     * @throws ValidatorNotFoundException         if no validator with given validatorId is found
     */
    VendorDTO getVendorByValidatorId(String validatorId) throws NoVendorForValidatorFoundException, ValidatorNotFoundException;
}
