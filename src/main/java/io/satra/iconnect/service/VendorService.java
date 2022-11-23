package io.satra.iconnect.service;

import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.dto.request.VendorRequestDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface VendorService {
    /**
     * This method is used to add a new vendor
     *
     * @param vendorRequestDTO the vendor information to register
     * @return the registered vendor {@link VendorDTO}
     * @throws BadRequestException if the vendor already exists
     */
    VendorDTO createVendor(VendorRequestDTO vendorRequestDTO) throws BadRequestException;

    /**
     * This method is used to update a vendor
     *
     * @param id the id of the vendor to be updated
     * @param vendorRequestDTO the vendor information to update
     * @return the updated vendor {@link VendorDTO}
     * @throws EntityNotFoundException if the vendor already exists
     */
    VendorDTO updateVendor(String id, VendorRequestDTO vendorRequestDTO) throws EntityNotFoundException;

    /**
     * This method is used to delete a vendor
     *
     * @param id the id of the vendor to be deleted
     * @throws EntityNotFoundException if the vendor does not exist
     */
    void deleteVendor(String id) throws EntityNotFoundException;

    /**
     * This method is used to get a vendor by given id
     *
     * @param id the id of the vendor to be obtained
     * @return the vendor data {@link VendorDTO}
     * @throws EntityNotFoundException if the vendor does not exist
     */
    VendorDTO findVendorById(String id) throws EntityNotFoundException;

    /**
     * This method is used to get all vendors
     *
     * @param pageable the pagination information
     * @return the list of vendors {@link VendorDTO}
     */
    Page<VendorDTO> findAllVendors(Pageable pageable);
}
