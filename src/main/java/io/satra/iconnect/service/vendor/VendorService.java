package io.satra.iconnect.service.vendor;

import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.entity.Vendor;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface VendorService {
    /**
     * This method is used to add a new vendor
     *
     * @param name the vendor name to register
     * @param logo the vendor logo
     * @return the registered vendor {@link VendorDTO}
     * @throws BadRequestException if the vendor already exists
     */
    VendorDTO createVendor(String name, MultipartFile logo) throws BadRequestException, IOException;

    /**
     * This method is used to update a vendor
     *
     * @param id the id of the vendor to be updated
     * @param name the vendor name to update
     * @param logo the vendor logo to update
     * @return the updated vendor {@link VendorDTO}
     * @throws EntityNotFoundException if the vendor already exists
     */
    VendorDTO updateVendor(String id, String name, MultipartFile logo) throws EntityNotFoundException, IOException;

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
     * This method is used to get vendor entity by given id
     *
     * @param id the id of the vendor to be obtained
     * @return the vendor entity {@link Vendor}
     * @throws EntityNotFoundException
     */
    Vendor findVendorEntityById(String id) throws EntityNotFoundException;

    /**
     * This method is used to get all vendors
     *
     * @param name the name of the vendor to be searched
     * @param page the pagination information
     * @return the list of vendors {@link VendorDTO}
     */
    Page<VendorDTO> findAllVendors(String name, Pageable page);
}
