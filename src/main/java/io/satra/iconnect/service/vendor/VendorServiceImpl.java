package io.satra.iconnect.service.vendor;

import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.entity.Vendor;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.VendorRepository;
import io.satra.iconnect.security.UserPrincipal;
import io.satra.iconnect.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    /**
     * This method is used to add a new vendor
     *
     * @param name the vendor name to register
     * @param logo the vendor logo
     * @return the registered vendor {@link VendorDTO}
     * @throws BadRequestException if the vendor already exists
     * @throws IOException        if the vendor logo cannot be saved
     */
    @Override
    public VendorDTO createVendor(String name, MultipartFile logo) throws BadRequestException, IOException {
        // check if the vendor request data is valid
        if (name == null || name.isEmpty()) {
            throw new BadRequestException("Vendor name is required");
        }

        // check if the vendor already exists
        if (vendorRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Vendor already exists");
        }

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

        // create the vendor entity
        Vendor vendor = Vendor.builder()
                .name(name)
                .createdBy(userPrincipal.getUser())
                .lastModifiedBy(userPrincipal.getUser())
                .build();

        // save the vendor
        vendor = vendorRepository.save(vendor);

        // check if the logo is not null
        if (logo != null) {
            // save the logo
            String logoPath = new FileUtils().saveFile(Collections.singletonList(logo), vendor.getId());
            log.info("Logo saved successfully at {}", logoPath);
            vendor.setLogo(logoPath);
            // save the vendor
            vendor = vendorRepository.save(vendor);
        }

        return vendor.toDTO();
    }

    /**
     * This method is used to update a vendor
     *
     * @param id the id of the vendor to be updated
     * @param name the vendor name to update
     * @param logo the vendor logo to update
     * @return the updated vendor {@link VendorDTO}
     * @throws EntityNotFoundException if the vendor already exists
     */
    @Override
    public VendorDTO updateVendor(String id, String name, MultipartFile logo) throws EntityNotFoundException, IOException {
        // check if the vendor not exists
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No vendor with given id %s found!", id)));

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

        // update the vendor name
        if (name != null && !name.isEmpty()) {
            vendor.setName(name);
        }

        // update the vendor logo
        if (logo != null) {
            // get the current logo path
            String currentLogoPath = vendor.getLogo();
            // save the logo
            String logoPath = new FileUtils().saveFile(Collections.singletonList(logo), vendor.getId());
            log.info("Logo saved successfully at {}", logoPath);
            // if the logo is saved successfully, add the new logo path to the vendor
            if (logoPath != null) {
                vendor.setLogo(logoPath);
            }
            // if the vendor has an old logo, delete it
            if (currentLogoPath != null) {
                new FileUtils().deleteFile(currentLogoPath);
            }
        }

        // update the vendor last modified by
        vendor.setLastModifiedBy(userPrincipal.getUser());

        // save the vendor
        vendor = vendorRepository.save(vendor);

        return vendor.toDTO();
    }

    /**
     * This method is used to delete a vendor
     *
     * @param id the id of the vendor to be deleted
     * @throws EntityNotFoundException if the vendor does not exist
     */
    @Override
    public void deleteVendor(String id) throws EntityNotFoundException {
        try {
            vendorRepository.deleteById(id);
        } catch (Exception e) {
            throw new EntityNotFoundException(String.format("No vendor with given id %s found!", id));
        }
    }

    /**
     * This method is used to get a vendor by given id
     *
     * @param id the id of the vendor to be obtained
     * @return the vendor data {@link VendorDTO}
     * @throws EntityNotFoundException if the vendor does not exist
     */
    @Override
    public VendorDTO findVendorById(String id) throws EntityNotFoundException {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No vendor with given id %s found!", id)));
        return vendor.toDTO();
    }

    /**
     * This method is used to get vendor entity by given id
     *
     * @param id the id of the vendor to be obtained
     * @return the vendor entity {@link Vendor}
     * @throws EntityNotFoundException if the vendor does not exist
     */
    @Override
    public Vendor findVendorEntityById(String id) throws EntityNotFoundException {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No vendor with given id %s found!", id)));
    }

    /**
     * This method is used to get all vendors
     *
     * @param name the name of the vendor to be searched
     * @param page the pagination information
     * @return the list of vendors {@link VendorDTO}
     */
    @Override
    public Page<VendorDTO> findAllVendors(String name, Pageable page) {
        // prepare the filter specification
        Specification<Vendor> specification = VendorSpecifications.filterVendors(name);
        return vendorRepository.findAll(specification, page).map(Vendor::toDTO);
    }
}
